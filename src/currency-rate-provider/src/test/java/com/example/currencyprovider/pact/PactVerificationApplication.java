package com.example.currencyprovider.pact;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.example.currencyprovider.service",
        "com.example.currencyprovider.pact"
})
public class PactVerificationApplication {
}
