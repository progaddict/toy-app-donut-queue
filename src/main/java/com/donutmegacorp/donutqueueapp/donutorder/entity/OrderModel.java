package com.donutmegacorp.donutqueueapp.donutorder.entity;

import com.donutmegacorp.donutqueueapp.donutorder.control.CustomerIdConstraint;
import com.donutmegacorp.donutqueueapp.donutorder.control.QuantityConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@lombok.Data
@lombok.Builder
@Jacksonized
public class OrderModel {
    @CustomerIdConstraint
    private Integer customerId;

    @QuantityConstraint
    private Integer quantity;

    @NotNull
    private Instant creationTimestamp;

    @NotNull
    private Integer queuePosition;

    @NotNull
    private Instant estimatedPickUpTimestamp;

    @JsonProperty(
            value = "estimatedWaitTimeMilliseconds",
            access = JsonProperty.Access.READ_ONLY
    )
    public Long getEstimatedWaitTimeMilliseconds() {
        if (Objects.isNull(creationTimestamp) || Objects.isNull(estimatedPickUpTimestamp)) {
            return null;
        }
        final Duration d = Duration.between(creationTimestamp, estimatedPickUpTimestamp);
        return d.toMillis();
    }
}
