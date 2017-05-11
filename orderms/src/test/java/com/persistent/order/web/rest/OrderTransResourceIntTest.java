package com.persistent.order.web.rest;

import com.persistent.order.OrdermsApp;

import com.persistent.order.domain.OrderTrans;
import com.persistent.order.repository.OrderTransRepository;
import com.persistent.order.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrderTransResource REST controller.
 *
 * @see OrderTransResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrdermsApp.class)
public class OrderTransResourceIntTest {

    private static final Integer DEFAULT_ORDER_ID = 1;
    private static final Integer UPDATED_ORDER_ID = 2;

    private static final Integer DEFAULT_PRODUCT_ID = 1;
    private static final Integer UPDATED_PRODUCT_ID = 2;

    @Autowired
    private OrderTransRepository orderTransRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOrderTransMockMvc;

    private OrderTrans orderTrans;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrderTransResource orderTransResource = new OrderTransResource(orderTransRepository);
        this.restOrderTransMockMvc = MockMvcBuilders.standaloneSetup(orderTransResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderTrans createEntity(EntityManager em) {
        OrderTrans orderTrans = new OrderTrans()
            .orderId(DEFAULT_ORDER_ID)
            .productId(DEFAULT_PRODUCT_ID);
        return orderTrans;
    }

    @Before
    public void initTest() {
        orderTrans = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderTrans() throws Exception {
        int databaseSizeBeforeCreate = orderTransRepository.findAll().size();

        // Create the OrderTrans
        restOrderTransMockMvc.perform(post("/api/order-trans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTrans)))
            .andExpect(status().isCreated());

        // Validate the OrderTrans in the database
        List<OrderTrans> orderTransList = orderTransRepository.findAll();
        assertThat(orderTransList).hasSize(databaseSizeBeforeCreate + 1);
        OrderTrans testOrderTrans = orderTransList.get(orderTransList.size() - 1);
        assertThat(testOrderTrans.getOrderId()).isEqualTo(DEFAULT_ORDER_ID);
        assertThat(testOrderTrans.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void createOrderTransWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderTransRepository.findAll().size();

        // Create the OrderTrans with an existing ID
        orderTrans.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderTransMockMvc.perform(post("/api/order-trans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTrans)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<OrderTrans> orderTransList = orderTransRepository.findAll();
        assertThat(orderTransList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkOrderIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderTransRepository.findAll().size();
        // set the field null
        orderTrans.setOrderId(null);

        // Create the OrderTrans, which fails.

        restOrderTransMockMvc.perform(post("/api/order-trans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTrans)))
            .andExpect(status().isBadRequest());

        List<OrderTrans> orderTransList = orderTransRepository.findAll();
        assertThat(orderTransList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderTransRepository.findAll().size();
        // set the field null
        orderTrans.setProductId(null);

        // Create the OrderTrans, which fails.

        restOrderTransMockMvc.perform(post("/api/order-trans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTrans)))
            .andExpect(status().isBadRequest());

        List<OrderTrans> orderTransList = orderTransRepository.findAll();
        assertThat(orderTransList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOrderTrans() throws Exception {
        // Initialize the database
        orderTransRepository.saveAndFlush(orderTrans);

        // Get all the orderTransList
        restOrderTransMockMvc.perform(get("/api/order-trans?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderTrans.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderId").value(hasItem(DEFAULT_ORDER_ID)))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID)));
    }

    @Test
    @Transactional
    public void getOrderTrans() throws Exception {
        // Initialize the database
        orderTransRepository.saveAndFlush(orderTrans);

        // Get the orderTrans
        restOrderTransMockMvc.perform(get("/api/order-trans/{id}", orderTrans.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(orderTrans.getId().intValue()))
            .andExpect(jsonPath("$.orderId").value(DEFAULT_ORDER_ID))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID));
    }

    @Test
    @Transactional
    public void getNonExistingOrderTrans() throws Exception {
        // Get the orderTrans
        restOrderTransMockMvc.perform(get("/api/order-trans/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderTrans() throws Exception {
        // Initialize the database
        orderTransRepository.saveAndFlush(orderTrans);
        int databaseSizeBeforeUpdate = orderTransRepository.findAll().size();

        // Update the orderTrans
        OrderTrans updatedOrderTrans = orderTransRepository.findOne(orderTrans.getId());
        updatedOrderTrans
            .orderId(UPDATED_ORDER_ID)
            .productId(UPDATED_PRODUCT_ID);

        restOrderTransMockMvc.perform(put("/api/order-trans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOrderTrans)))
            .andExpect(status().isOk());

        // Validate the OrderTrans in the database
        List<OrderTrans> orderTransList = orderTransRepository.findAll();
        assertThat(orderTransList).hasSize(databaseSizeBeforeUpdate);
        OrderTrans testOrderTrans = orderTransList.get(orderTransList.size() - 1);
        assertThat(testOrderTrans.getOrderId()).isEqualTo(UPDATED_ORDER_ID);
        assertThat(testOrderTrans.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingOrderTrans() throws Exception {
        int databaseSizeBeforeUpdate = orderTransRepository.findAll().size();

        // Create the OrderTrans

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOrderTransMockMvc.perform(put("/api/order-trans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTrans)))
            .andExpect(status().isCreated());

        // Validate the OrderTrans in the database
        List<OrderTrans> orderTransList = orderTransRepository.findAll();
        assertThat(orderTransList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOrderTrans() throws Exception {
        // Initialize the database
        orderTransRepository.saveAndFlush(orderTrans);
        int databaseSizeBeforeDelete = orderTransRepository.findAll().size();

        // Get the orderTrans
        restOrderTransMockMvc.perform(delete("/api/order-trans/{id}", orderTrans.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<OrderTrans> orderTransList = orderTransRepository.findAll();
        assertThat(orderTransList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderTrans.class);
    }
}
