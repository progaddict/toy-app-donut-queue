package com.donutmegacorp.donutqueueapp.donutorder.entity;

import com.donutmegacorp.donutqueueapp.donutorder.control.CustomerIdConstraint;
import com.donutmegacorp.donutqueueapp.donutorder.control.QuantityConstraint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "t_order")
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class OrderEntity {
    @Id
    @Column(name = "customer_id", nullable = false, unique = true)
    @CustomerIdConstraint
    private Integer customerId;

    @Column(name = "quantity", nullable = false)
    @QuantityConstraint
    private Integer quantity;

    @Column(name = "creation_timestamp", nullable = false)
    @NotNull
    private Instant creationTimestamp;
}
