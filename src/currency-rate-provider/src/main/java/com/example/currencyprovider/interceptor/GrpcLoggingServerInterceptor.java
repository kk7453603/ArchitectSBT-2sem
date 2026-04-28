package com.example.currencyprovider.interceptor;

import io.grpc.ForwardingServerCall;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gRPC server interceptor that logs incoming requests and outgoing responses.
 *
 * Logs at INFO level:
 * - Incoming: method name + client address
 * - Outgoing (via ServerCall.close): response status + duration in ms
 */
@GrpcGlobalServerInterceptor
public class GrpcLoggingServerInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GrpcLoggingServerInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        long startTime = System.nanoTime();
        String methodName = call.getMethodDescriptor().getFullMethodName();
        Object clientAddress = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);

        logger.info("Incoming gRPC request: method={}, client={}", methodName, clientAddress);

        ServerCall<ReqT, RespT> loggingCall =
                new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    @Override
                    public void close(Status status, Metadata trailers) {
                        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                        logger.info("gRPC response: method={}, status={}, duration={}ms",
                                methodName, status.getCode(), durationMs);
                        super.close(status, trailers);
                    }
                };

        return next.startCall(loggingCall, headers);
    }
}
