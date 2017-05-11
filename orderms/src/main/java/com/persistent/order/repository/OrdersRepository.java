package com.persistent.order.repository;

import com.persistent.order.domain.Orders;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Orders entity.
 */
@SuppressWarnings("unused")
public interface OrdersRepository extends JpaRepository<Orders,Long> {

}
