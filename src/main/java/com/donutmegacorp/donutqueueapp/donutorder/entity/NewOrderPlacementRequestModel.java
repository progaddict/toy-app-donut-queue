package com.donutmegacorp.donutqueueapp.donutorder.entity;

import com.donutmegacorp.donutqueueapp.donutorder.control.CustomerIdConstraint;
import com.donutmegacorp.donutqueueapp.donutorder.control.QuantityConstraint;
import lombok.extern.jackson.Jacksonized;

@lombok.Data
@lombok.Builder
@Jacksonized
public class NewOrderPlacementRequestModel {
    @CustomerIdConstraint
    private Integer customerId;

    @QuantityConstraint
    private Integer quantity;
}
