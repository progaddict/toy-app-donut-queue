package com.donutmegacorp.donutqueueapp.donutorder.control;

import com.donutmegacorp.donutqueueapp.donutorder.entity.NewOrderPlacementRequestModel;
import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderEntity;
import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@lombok.RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OrdersService {
    private static final int PREMIUM_CUSTOMER_ID_THRESHOLD = 1000;
    private static final int MAX_DONUTS_PER_BASKET = 50;
    private static final Duration SERVICING_INTERVAL = Duration.ofMinutes(5);

    private final Clock clock;
    private final OrderEntityRepository orderEntityRepository;
    private final ModelMapper mapper;

    @Transactional
    public void saveNewOrder(final NewOrderPlacementRequestModel model) {
        final int customerId = model.getCustomerId();
        if (orderEntityRepository.findById(customerId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "order for customer with ID %s already exists".formatted(customerId)
            );
        }
        final OrderEntity newOrder = mapper.mapNewOrderPlacementRequestModelToOrderEntity(model);
        newOrder.setCreationTimestamp(clock.instant());
        orderEntityRepository.save(newOrder);
    }

    @Transactional
    public boolean deleteOrder(final int customerId) {
        try {
            orderEntityRepository.deleteById(customerId);
            return true;
        } catch (final EmptyResultDataAccessException error) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public Optional<OrderModel> getOrder(final int customerId) {
        return getAllOrders().stream()
                .filter(orderModel -> customerId == orderModel.getCustomerId())
                .findAny();
    }

    @Transactional(readOnly = true)
    public List<OrderModel> getOrdersForNextDelivery() {
        return getAllOrders().stream()
                .filter(orderModel -> 1 == orderModel.getQueuePosition())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderModel> getAllOrders() {
        final List<OrderModel> orders = orderEntityRepository.getAllOrdersSortedByPriority(PREMIUM_CUSTOMER_ID_THRESHOLD)
                .stream()
                .map(mapper::mapOrderEntityToOrderModel)
                .collect(Collectors.toList());
        // model servicing the orders
        final int totalOrdersCount = orders.size();
        int queuePosition = 1;
        Instant pickupTimestamp = clock.instant();
        int i = 0;
        while (i < totalOrdersCount) {
            int quantity = 0;
            while (
                    i < totalOrdersCount
                            && quantity + orders.get(i).getQuantity() <= MAX_DONUTS_PER_BASKET
            ) {
                final OrderModel order = orders.get(i);
                order.setQueuePosition(queuePosition);
                order.setEstimatedPickUpTimestamp(pickupTimestamp);
                quantity += order.getQuantity();
                i++;
            }
            LOG.debug(
                    "queuePosition = {}, pickupTimestamp = {}, quantity = {}, i = {}",
                    queuePosition,
                    pickupTimestamp,
                    quantity,
                    i
            );
            queuePosition++;
            pickupTimestamp = pickupTimestamp.plus(SERVICING_INTERVAL);
        }
        return orders;
    }
}
