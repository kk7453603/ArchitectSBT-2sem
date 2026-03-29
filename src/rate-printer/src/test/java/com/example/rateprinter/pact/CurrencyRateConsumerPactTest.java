package com.example.rateprinter.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "currency-rate-provider")
class CurrencyRateConsumerPactTest {

    @Pact(consumer = "rate-printer", provider = "currency-rate-provider")
    V4Pact currencyRatePact(PactDslWithProvider builder) {
        return builder
                .given("provider is running")
                .uponReceiving("a request for currency rate")
                .path("/rate")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(body -> {
                    body.decimalType("rate", 92.5);
                    body.numberType("timestamp", 1700000000000L);
                }).build())
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "currencyRatePact")
    void testCurrencyRateContract(MockServer mockServer) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(mockServer.getUrl() + "/rate"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        String body = response.body();
        assertTrue(body.contains("rate"));
        assertTrue(body.contains("timestamp"));
    }
}
