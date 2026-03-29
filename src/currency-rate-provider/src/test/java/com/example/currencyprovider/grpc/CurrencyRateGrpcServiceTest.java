package com.example.currencyprovider.grpc;

import com.example.currencyprovider.service.CurrencyRateService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateGrpcServiceTest {

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private CurrencyRateGrpcService grpcService;

    @Test
    void getRateShouldReturnResponseWhenServiceSucceeds() {
        when(currencyRateService.getCurrentRate()).thenReturn(new CurrencyRateService.RateData(92.45, 1_700_000_000_000L));
        TestObserver observer = new TestObserver();

        grpcService.getRate(EmptyRequest.getDefaultInstance(), observer);

        assertThat(observer.values).hasSize(1);
        assertThat(observer.values.get(0).getRate()).isEqualTo(92.45);
        assertThat(observer.values.get(0).getTimestamp()).isEqualTo(1_700_000_000_000L);
        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
    }

    @Test
    void getRateShouldReturnInternalErrorWhenServiceThrowsException() {
        when(currencyRateService.getCurrentRate()).thenThrow(new RuntimeException("boom"));
        TestObserver observer = new TestObserver();

        grpcService.getRate(EmptyRequest.getDefaultInstance(), observer);

        assertThat(observer.values).isEmpty();
        assertThat(observer.completed).isFalse();
        assertThat(observer.error).isInstanceOf(StatusRuntimeException.class);
        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) observer.error;
        assertThat(statusRuntimeException.getStatus().getCode()).isEqualTo(Status.Code.INTERNAL);
    }

    private static final class TestObserver implements StreamObserver<CurrencyRateResponse> {

        private final List<CurrencyRateResponse> values = new ArrayList<>();
        private Throwable error;
        private boolean completed;

        @Override
        public void onNext(CurrencyRateResponse value) {
            values.add(value);
        }

        @Override
        public void onError(Throwable throwable) {
            this.error = throwable;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }
    }
}
