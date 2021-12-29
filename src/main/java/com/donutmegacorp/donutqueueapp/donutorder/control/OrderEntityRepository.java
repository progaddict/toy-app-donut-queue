package com.donutmegacorp.donutqueueapp.donutorder.control;

import com.donutmegacorp.donutqueueapp.donutorder.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Integer> {
    @Query(
            " SELECT oe, ( oe.customerId < :premiumCustomerIdThreshold ) as isPremiumCustomer "
                    + " FROM OrderEntity oe "
                    + " ORDER BY isPremiumCustomer DESC, oe.creationTimestamp ASC "
    )
    List<OrderEntity> getAllOrdersSortedByPriority(
            @Param("premiumCustomerIdThreshold") final int premiumCustomerIdThreshold
    );
}
