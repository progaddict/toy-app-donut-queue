package com.donutmegacorp.donutqueueapp.donutorder.control;

import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderEntity;
import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example of a unit test.
 */
@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {
    @Mock
    private Clock clock;

    @Mock
    private OrderEntityRepository orderEntityRepository;

    @Spy
    private ModelMapper mapper = Mappers.getMapper(ModelMapper.class);

    @InjectMocks
    private OrdersService sut;

    @Test
    void getAllOrders() {
        // GIVEN
        final OrderEntity order1 = new OrderEntity();
        order1.setCustomerId(1);
        order1.setQuantity(20);
        order1.setCreationTimestamp(Instant.parse("2021-12-01T10:00:00.00Z"));
        final OrderEntity order2 = new OrderEntity();
        order2.setCustomerId(2);
        order2.setQuantity(30);
        order2.setCreationTimestamp(Instant.parse("2021-12-03T10:00:00.00Z"));
        final OrderEntity order3 = new OrderEntity();
        order3.setCustomerId(3);
        order3.setQuantity(40);
        order3.setCreationTimestamp(Instant.parse("2021-12-05T10:00:00.00Z"));
        Mockito.when(orderEntityRepository.getAllOrdersSortedByPriority(Mockito.anyInt()))
                .thenReturn(List.of(order1, order2, order3));
        Mockito.when(clock.instant())
                .thenReturn(Instant.parse("2021-12-07T10:00:00.00Z"));
        // WHEN
        final List<OrderModel> result = sut.getAllOrders();
        // THEN
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(
                // this works because @lombok.Data generates equals()
                OrderModel.builder()
                        .customerId(1)
                        .quantity(20)
                        .creationTimestamp(Instant.parse("2021-12-01T10:00:00.00Z"))
                        .queuePosition(1)
                        .estimatedPickUpTimestamp(Instant.parse("2021-12-07T10:00:00.00Z"))
                        .build()
        );
        assertThat(result.get(1)).isEqualTo(
                OrderModel.builder()
                        .customerId(2)
                        .quantity(30)
                        .creationTimestamp(Instant.parse("2021-12-03T10:00:00.00Z"))
                        .queuePosition(1)
                        .estimatedPickUpTimestamp(Instant.parse("2021-12-07T10:00:00.00Z"))
                        .build()
        );
        assertThat(result.get(2)).isEqualTo(
                OrderModel.builder()
                        .customerId(3)
                        .quantity(40)
                        .creationTimestamp(Instant.parse("2021-12-05T10:00:00.00Z"))
                        .queuePosition(2)
                        .estimatedPickUpTimestamp(Instant.parse("2021-12-07T10:05:00.00Z"))
                        .build()
        );
        Mockito.verify(orderEntityRepository).getAllOrdersSortedByPriority(1000);
        final ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        Mockito.verify(mapper, Mockito.times(3)).mapOrderEntityToOrderModel(captor.capture());
        final List<OrderEntity> orderEntities = captor.getAllValues();
        assertThat(orderEntities).hasSize(3);
        assertThat(orderEntities.get(0)).isEqualTo(order1);
        assertThat(orderEntities.get(1)).isEqualTo(order2);
        assertThat(orderEntities.get(2)).isEqualTo(order3);
    }
}
