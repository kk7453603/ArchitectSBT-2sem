package com.example.currencyprovider.interceptor;

import io.grpc.ForwardingServerCall;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

/**
 * gRPC server interceptor that records Micrometer metrics for every call.
 *
 * Metrics:
 * - grpc.server.requests  — Counter, tags: method, client_address (every call)
 * - grpc.server.errors    — Counter, tags: method, status_code     (non-OK calls only)
 * - grpc.server.request.duration — Timer, tag: method, percentiles 0.5/0.95/0.99
 */
@GrpcGlobalServerInterceptor
public class GrpcMetricsServerInterceptor implements ServerInterceptor {

    private final MeterRegistry registry;

    public GrpcMetricsServerInterceptor(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String methodName = call.getMethodDescriptor().getFullMethodName();
        String rawAddr = String.valueOf(call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
        // Strip ephemeral port to avoid cardinality explosion, e.g. "/10.0.0.5:54321" -> "10.0.0.5"
        String clientAddress = rawAddr.contains(":")
                ? rawAddr.substring(0, rawAddr.lastIndexOf(':')).replaceFirst("^/", "")
                : rawAddr;

        // Increment request counter for every incoming call
        Counter.builder("grpc.server.requests")
                .tag("method", methodName)
                .tag("client_address", clientAddress)
                .register(registry)
                .increment();

        // Build the duration timer (with percentiles) and start a sample
        Timer durationTimer = Timer.builder("grpc.server.request.duration")
                .tag("method", methodName)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);

        Timer.Sample sample = Timer.start(registry);

        ServerCall<ReqT, RespT> metricsCall =
                new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    @Override
                    public void close(Status status, Metadata trailers) {
                        // Stop the duration timer
                        sample.stop(durationTimer);

                        // Increment error counter for non-OK responses
                        if (status.getCode() != Status.Code.OK) {
                            Counter.builder("grpc.server.errors")
                                    .tag("method", methodName)
                                    .tag("status_code", status.getCode().name())
                                    .register(registry)
                                    .increment();
                        }

                        super.close(status, trailers);
                    }
                };

        return next.startCall(metricsCall, headers);
    }
}
