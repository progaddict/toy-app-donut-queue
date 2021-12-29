package com.donutmegacorp.donutqueueapp.donutorder.control;

import com.donutmegacorp.donutqueueapp.donutorder.entity.NewOrderPlacementRequestModel;
import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderEntity;
import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderModel;
import org.mapstruct.Mapper;

@Mapper
public interface ModelMapper {
    OrderEntity mapNewOrderPlacementRequestModelToOrderEntity(final NewOrderPlacementRequestModel model);

    OrderModel mapOrderEntityToOrderModel(final OrderEntity entity);
}
