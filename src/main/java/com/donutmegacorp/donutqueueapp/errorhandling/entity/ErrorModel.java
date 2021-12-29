package com.donutmegacorp.donutqueueapp.errorhandling.entity;

import lombok.extern.jackson.Jacksonized;

@lombok.Data
@lombok.Builder
@Jacksonized
public class ErrorModel {
    private final String traceId;
    private final String message;
}
