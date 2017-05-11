package com.persistent.order.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.persistent.order.domain.OrderTrans;

import com.persistent.order.repository.OrderTransRepository;
import com.persistent.order.web.rest.util.HeaderUtil;
import com.persistent.order.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing OrderTrans.
 */
@RestController
@RequestMapping("/api")
public class OrderTransResource {

    private final Logger log = LoggerFactory.getLogger(OrderTransResource.class);

    private static final String ENTITY_NAME = "orderTrans";
        
    private final OrderTransRepository orderTransRepository;

    public OrderTransResource(OrderTransRepository orderTransRepository) {
        this.orderTransRepository = orderTransRepository;
    }

    /**
     * POST  /order-trans : Create a new orderTrans.
     *
     * @param orderTrans the orderTrans to create
     * @return the ResponseEntity with status 201 (Created) and with body the new orderTrans, or with status 400 (Bad Request) if the orderTrans has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/order-trans")
    @Timed
    public ResponseEntity<OrderTrans> createOrderTrans(@Valid @RequestBody OrderTrans orderTrans) throws URISyntaxException {
        log.debug("REST request to save OrderTrans : {}", orderTrans);
        if (orderTrans.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new orderTrans cannot already have an ID")).body(null);
        }
        OrderTrans result = orderTransRepository.save(orderTrans);
        return ResponseEntity.created(new URI("/api/order-trans/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /order-trans : Updates an existing orderTrans.
     *
     * @param orderTrans the orderTrans to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated orderTrans,
     * or with status 400 (Bad Request) if the orderTrans is not valid,
     * or with status 500 (Internal Server Error) if the orderTrans couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/order-trans")
    @Timed
    public ResponseEntity<OrderTrans> updateOrderTrans(@Valid @RequestBody OrderTrans orderTrans) throws URISyntaxException {
        log.debug("REST request to update OrderTrans : {}", orderTrans);
        if (orderTrans.getId() == null) {
            return createOrderTrans(orderTrans);
        }
        OrderTrans result = orderTransRepository.save(orderTrans);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, orderTrans.getId().toString()))
            .body(result);
    }

    /**
     * GET  /order-trans : get all the orderTrans.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of orderTrans in body
     */
    @GetMapping("/order-trans")
    @Timed
    public ResponseEntity<List<OrderTrans>> getAllOrderTrans(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of OrderTrans");
        Page<OrderTrans> page = orderTransRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/order-trans");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /order-trans/:id : get the "id" orderTrans.
     *
     * @param id the id of the orderTrans to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orderTrans, or with status 404 (Not Found)
     */
    @GetMapping("/order-trans/{id}")
    @Timed
    public ResponseEntity<OrderTrans> getOrderTrans(@PathVariable Long id) {
        log.debug("REST request to get OrderTrans : {}", id);
        OrderTrans orderTrans = orderTransRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderTrans));
    }

    /**
     * DELETE  /order-trans/:id : delete the "id" orderTrans.
     *
     * @param id the id of the orderTrans to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/order-trans/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrderTrans(@PathVariable Long id) {
        log.debug("REST request to delete OrderTrans : {}", id);
        orderTransRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
