package com.donutmegacorp.donutqueueapp.donutorder.boundary;

import com.donutmegacorp.donutqueueapp.donutorder.control.CustomerIdConstraint;
import com.donutmegacorp.donutqueueapp.donutorder.control.OrdersService;
import com.donutmegacorp.donutqueueapp.donutorder.entity.NewOrderPlacementRequestModel;
import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(OrderController.URL_PREFIX)
@lombok.RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {
    public static final String URL_PREFIX = "/api/v1/order";

    private final OrdersService ordersService;

    /**
     * An endpoint for adding items to the queue.
     * This endpoint must take two parameters,
     * the ID of the client and the quantity.
     */
    @PostMapping
    public ResponseEntity<OrderModel> placeNewOrder(
            @NotNull @Valid @RequestBody final NewOrderPlacementRequestModel model
    ) {
        ordersService.saveNewOrder(model);
        final OrderModel order = ordersService.getOrder(model.getCustomerId()).orElseThrow(
                () -> new IllegalStateException(String.format(
                        "order for customer with ID %s"
                                + " has been deleted by a parallel transaction"
                                + " milliseconds ago. bad luck :(",
                        model.getCustomerId()
                ))
        );
        return ResponseEntity.ok(order);
    }

    /**
     * An endpoint to cancel an order.
     * This endpoint should accept only the client ID.
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Boolean> deleteOrder(
            @CustomerIdConstraint @PathVariable("customerId") final Integer customerId
    ) {
        if (ordersService.deleteOrder(customerId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * An endpoint which allows
     * his manager to see
     * all entries in the queue with the
     * approximate wait time.
     */
    @GetMapping
    public ResponseEntity<List<OrderModel>> getAllOrders() {
        return ResponseEntity.ok(ordersService.getAllOrders());
    }

    /**
     * An endpoint to retrieve his next
     * delivery which should be placed in the cart.
     */
    @GetMapping("/next-delivery")
    public ResponseEntity<List<OrderModel>> getOrdersForNextDelivery() {
        return ResponseEntity.ok(ordersService.getOrdersForNextDelivery());
    }

    /**
     * An endpoint for the client
     * to check his queue position and approximate wait time.
     * Counting starts at 1.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<OrderModel> getOrder(
            @CustomerIdConstraint @PathVariable("customerId") final Integer customerId
    ) {
        return ordersService.getOrder(customerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
