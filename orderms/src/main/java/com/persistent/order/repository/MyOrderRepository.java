package com.persistent.order.repository;

import com.persistent.order.domain.MyOrder;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the MyOrder entity.
 */
@SuppressWarnings("unused")
public interface MyOrderRepository extends JpaRepository<MyOrder,Long> {

}
