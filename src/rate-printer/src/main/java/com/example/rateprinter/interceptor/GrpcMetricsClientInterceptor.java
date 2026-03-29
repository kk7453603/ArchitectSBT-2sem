package com.example.rateprinter.interceptor;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class GrpcMetricsClientInterceptor implements ClientInterceptor {

    private final MeterRegistry meterRegistry;

    public GrpcMetricsClientInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        String methodName = method.getFullMethodName();

        Counter.builder("grpc.client.requests")
                .tag("method", methodName)
                .register(meterRegistry)
                .increment();

        Timer.Sample sample = Timer.start(meterRegistry);

        ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Listener<RespT> wrappedListener =
                        new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                            @Override
                            public void onClose(Status status, Metadata trailers) {
                                Timer timer = Timer.builder("grpc.client.request.duration")
                                        .tag("method", methodName)
                                        .publishPercentiles(0.5, 0.95, 0.99)
                                        .register(meterRegistry);
                                sample.stop(timer);
                                super.onClose(status, trailers);
                            }
                        };
                super.start(wrappedListener, headers);
            }
        };
    }
}
