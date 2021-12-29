package com.donutmegacorp.donutqueueapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class TimeConfiguration {
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
