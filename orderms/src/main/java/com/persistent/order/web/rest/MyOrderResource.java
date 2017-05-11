package com.persistent.order.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.persistent.order.domain.MyOrder;
import com.persistent.order.rabbitmq.RabbitMqConfiguration;
import com.persistent.order.repository.MyOrderRepository;
import com.persistent.order.web.rest.util.HeaderUtil;
import com.persistent.order.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing MyOrder.
 */
@RestController
@RequestMapping("/api")
public class MyOrderResource {

    private final Logger log = LoggerFactory.getLogger(MyOrderResource.class);

    private static final String ENTITY_NAME = "myOrder";
        
    private final MyOrderRepository myOrderRepository;
    
    private final RabbitTemplate rabbitTemplate;

    public MyOrderResource(MyOrderRepository myOrderRepository, RabbitTemplate rabbitTemplate) {
        this.myOrderRepository = myOrderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * POST  /my-orders : Create a new myOrder.
     *
     * @param myOrder the myOrder to create
     * @return the ResponseEntity with status 201 (Created) and with body the new myOrder, or with status 400 (Bad Request) if the myOrder has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/my-orders")
    @Timed
    public ResponseEntity<MyOrder> createMyOrder(@RequestBody MyOrder myOrder) throws URISyntaxException {
        log.debug("REST request to save MyOrder : {}", myOrder);
        if (myOrder.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new myOrder cannot already have an ID")).body(null);
        }
        MyOrder result = myOrderRepository.save(myOrder);
        
        Map<String, String> map  = new HashMap<String,String>();
        map.put("1", myOrder.toString());
        rabbitTemplate.convertAndSend(RabbitMqConfiguration.ORDER_QUEUE, map);
        
        return ResponseEntity.created(new URI("/api/my-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /my-orders : Updates an existing myOrder.
     *
     * @param myOrder the myOrder to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated myOrder,
     * or with status 400 (Bad Request) if the myOrder is not valid,
     * or with status 500 (Internal Server Error) if the myOrder couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/my-orders")
    @Timed
    public ResponseEntity<MyOrder> updateMyOrder(@RequestBody MyOrder myOrder) throws URISyntaxException {
        log.debug("REST request to update MyOrder : {}", myOrder);
        if (myOrder.getId() == null) {
            return createMyOrder(myOrder);
        }
        MyOrder result = myOrderRepository.save(myOrder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, myOrder.getId().toString()))
            .body(result);
    }

    /**
     * GET  /my-orders : get all the myOrders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of myOrders in body
     */
    @GetMapping("/my-orders")
    @Timed
    public ResponseEntity<List<MyOrder>> getAllMyOrders(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of MyOrders");
        Page<MyOrder> page = myOrderRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/my-orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /my-orders/:id : get the "id" myOrder.
     *
     * @param id the id of the myOrder to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the myOrder, or with status 404 (Not Found)
     */
    @GetMapping("/my-orders/{id}")
    @Timed
    public ResponseEntity<MyOrder> getMyOrder(@PathVariable Long id) {
        log.debug("REST request to get MyOrder : {}", id);
        MyOrder myOrder = myOrderRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(myOrder));
    }

    /**
     * DELETE  /my-orders/:id : delete the "id" myOrder.
     *
     * @param id the id of the myOrder to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/my-orders/{id}")
    @Timed
    public ResponseEntity<Void> deleteMyOrder(@PathVariable Long id) {
        log.debug("REST request to delete MyOrder : {}", id);
        myOrderRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
