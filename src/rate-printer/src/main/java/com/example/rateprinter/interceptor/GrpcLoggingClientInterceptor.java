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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GrpcLoggingClientInterceptor implements ClientInterceptor {

    private static final Logger log = LoggerFactory.getLogger(GrpcLoggingClientInterceptor.class);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        String methodName = method.getFullMethodName();
        String authority = next.authority();

        log.info("gRPC outgoing request: method={}, authority={}", methodName, authority);

        long startTime = System.nanoTime();

        ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Listener<RespT> wrappedListener =
                        new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                            @Override
                            public void onClose(Status status, Metadata trailers) {
                                long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                                log.info("gRPC response: method={}, status={}, durationMs={}",
                                        methodName, status.getCode(), durationMs);
                                super.onClose(status, trailers);
                            }
                        };
                super.start(wrappedListener, headers);
            }
        };
    }
}
