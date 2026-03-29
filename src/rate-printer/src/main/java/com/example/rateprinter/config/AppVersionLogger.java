package com.example.rateprinter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Application version logger component.
 *
 * Logs the application name and version on startup.
 */
@Component
public class AppVersionLogger implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppVersionLogger.class);

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Application started: {} v{}", appName, appVersion);
    }
}
