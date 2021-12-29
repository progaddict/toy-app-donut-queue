package com.donutmegacorp.donutqueueapp;

import com.donutmegacorp.donutqueueapp.donutorder.boundary.OrderController;
import com.donutmegacorp.donutqueueapp.donutorder.control.OrderEntityRepository;
import com.donutmegacorp.donutqueueapp.donutorder.entity.NewOrderPlacementRequestModel;
import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DonutqueueappApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderEntityRepository orderEntityRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        orderEntityRepository.deleteAll();
        baseUrl = "http://127.0.0.1:" + port;
    }

    @Test
    void contextLoads() {
    }

    /**
     * Example of a simple integration test.
     * For more complex cases I'd use
     * <a href="https://citrusframework.org/">Citrus Framework</a>.
     */
    @Test
    void simpleIntegrationTest() {
        // at the beginning there's nothing in there
        assertThat(getOrders()).isEmpty();

        // add some orders
        restTemplate.postForObject(
                baseUrl + OrderController.URL_PREFIX,
                NewOrderPlacementRequestModel.builder().customerId(1).quantity(10).build(),
                Void.class
        );
        restTemplate.postForObject(
                baseUrl + OrderController.URL_PREFIX,
                NewOrderPlacementRequestModel.builder().customerId(1001).quantity(15).build(),
                Void.class
        );
        restTemplate.postForObject(
                baseUrl + OrderController.URL_PREFIX,
                NewOrderPlacementRequestModel.builder().customerId(2).quantity(20).build(),
                Void.class
        );
        restTemplate.postForObject(
                baseUrl + OrderController.URL_PREFIX,
                NewOrderPlacementRequestModel.builder().customerId(1002).quantity(25).build(),
                Void.class
        );
        restTemplate.postForObject(
                baseUrl + OrderController.URL_PREFIX,
                NewOrderPlacementRequestModel.builder().customerId(3).quantity(30).build(),
                Void.class
        );
        restTemplate.postForObject(
                baseUrl + OrderController.URL_PREFIX,
                NewOrderPlacementRequestModel.builder().customerId(1003).quantity(35).build(),
                Void.class
        );
        restTemplate.postForObject(
                baseUrl + OrderController.URL_PREFIX,
                NewOrderPlacementRequestModel.builder().customerId(4).quantity(40).build(),
                Void.class
        );

        // check orders have been saved
        final List<OrderModel> orders = getOrders();
        assertThat(orders.stream().map(OrderModel::getCustomerId).toList())
                .isEqualTo(List.of(1, 2, 3, 4, 1001, 1002, 1003));
        assertThat(orders.stream().map(OrderModel::getQueuePosition).toList())
                .isEqualTo(List.of(1, 1, 2, 3, 4, 4, 5));

        // trying to add additional order (same client ID) should fail
        final ResponseEntity<String> response01 = restTemplate.postForEntity(
                baseUrl + OrderController.URL_PREFIX,
                NewOrderPlacementRequestModel.builder().customerId(4).quantity(11).build(),
                String.class
        );
        assertThat(response01.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // check next delivery endpoint
        final List<OrderModel> ordersForNextDelivery = Arrays.stream(
                restTemplate.getForObject(
                        baseUrl + OrderController.URL_PREFIX + "/next-delivery",
                        OrderModel[].class
                )
        ).toList();
        assertThat(ordersForNextDelivery).hasSize(2);
        assertThat(ordersForNextDelivery.stream().map(OrderModel::getCustomerId).toList())
                .isEqualTo(List.of(1, 2));

        // delete orders
        restTemplate.delete(baseUrl + OrderController.URL_PREFIX + "/1");
        restTemplate.delete(baseUrl + OrderController.URL_PREFIX + "/2");
        restTemplate.delete(baseUrl + OrderController.URL_PREFIX + "/3");
        restTemplate.delete(baseUrl + OrderController.URL_PREFIX + "/4");
        restTemplate.delete(baseUrl + OrderController.URL_PREFIX + "/1001");
        restTemplate.delete(baseUrl + OrderController.URL_PREFIX + "/1002");
        restTemplate.delete(baseUrl + OrderController.URL_PREFIX + "/1003");
        assertThat(getOrders()).isEmpty();
    }

    private List<OrderModel> getOrders() {
        final OrderModel[] response = restTemplate.getForObject(
                baseUrl + OrderController.URL_PREFIX,
                OrderModel[].class
        );
        return Arrays.stream(response).toList();
    }
}
