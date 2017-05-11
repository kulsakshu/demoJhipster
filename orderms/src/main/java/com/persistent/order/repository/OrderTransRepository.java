package com.persistent.order.repository;

import com.persistent.order.domain.OrderTrans;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the OrderTrans entity.
 */
@SuppressWarnings("unused")
public interface OrderTransRepository extends JpaRepository<OrderTrans,Long> {

}
