package com.persistent.order.web.rest;

import com.persistent.order.OrdermsApp;

import com.persistent.order.domain.MyOrder;
import com.persistent.order.repository.MyOrderRepository;
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
 * Test class for the MyOrderResource REST controller.
 *
 * @see MyOrderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrdermsApp.class)
public class MyOrderResourceIntTest {

    private static final Integer DEFAULT_CUST_ID = 1;
    private static final Integer UPDATED_CUST_ID = 2;

    private static final Integer DEFAULT_PROD_CNT = 1;
    private static final Integer UPDATED_PROD_CNT = 2;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    @Autowired
    private MyOrderRepository myOrderRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMyOrderMockMvc;

    private MyOrder myOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MyOrderResource myOrderResource = new MyOrderResource(myOrderRepository,null);
        this.restMyOrderMockMvc = MockMvcBuilders.standaloneSetup(myOrderResource)
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
    public static MyOrder createEntity(EntityManager em) {
        MyOrder myOrder = new MyOrder()
            .custId(DEFAULT_CUST_ID)
            .prodCnt(DEFAULT_PROD_CNT)
            .status(DEFAULT_STATUS);
        return myOrder;
    }

    @Before
    public void initTest() {
        myOrder = createEntity(em);
    }

    @Test
    @Transactional
    public void createMyOrder() throws Exception {
        int databaseSizeBeforeCreate = myOrderRepository.findAll().size();

        // Create the MyOrder
        restMyOrderMockMvc.perform(post("/api/my-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(myOrder)))
            .andExpect(status().isCreated());

        // Validate the MyOrder in the database
        List<MyOrder> myOrderList = myOrderRepository.findAll();
        assertThat(myOrderList).hasSize(databaseSizeBeforeCreate + 1);
        MyOrder testMyOrder = myOrderList.get(myOrderList.size() - 1);
        assertThat(testMyOrder.getCustId()).isEqualTo(DEFAULT_CUST_ID);
        assertThat(testMyOrder.getProdCnt()).isEqualTo(DEFAULT_PROD_CNT);
        assertThat(testMyOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createMyOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = myOrderRepository.findAll().size();

        // Create the MyOrder with an existing ID
        myOrder.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMyOrderMockMvc.perform(post("/api/my-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(myOrder)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<MyOrder> myOrderList = myOrderRepository.findAll();
        assertThat(myOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMyOrders() throws Exception {
        // Initialize the database
        myOrderRepository.saveAndFlush(myOrder);

        // Get all the myOrderList
        restMyOrderMockMvc.perform(get("/api/my-orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(myOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].custId").value(hasItem(DEFAULT_CUST_ID)))
            .andExpect(jsonPath("$.[*].prodCnt").value(hasItem(DEFAULT_PROD_CNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getMyOrder() throws Exception {
        // Initialize the database
        myOrderRepository.saveAndFlush(myOrder);

        // Get the myOrder
        restMyOrderMockMvc.perform(get("/api/my-orders/{id}", myOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(myOrder.getId().intValue()))
            .andExpect(jsonPath("$.custId").value(DEFAULT_CUST_ID))
            .andExpect(jsonPath("$.prodCnt").value(DEFAULT_PROD_CNT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMyOrder() throws Exception {
        // Get the myOrder
        restMyOrderMockMvc.perform(get("/api/my-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMyOrder() throws Exception {
        // Initialize the database
        myOrderRepository.saveAndFlush(myOrder);
        int databaseSizeBeforeUpdate = myOrderRepository.findAll().size();

        // Update the myOrder
        MyOrder updatedMyOrder = myOrderRepository.findOne(myOrder.getId());
        updatedMyOrder
            .custId(UPDATED_CUST_ID)
            .prodCnt(UPDATED_PROD_CNT)
            .status(UPDATED_STATUS);

        restMyOrderMockMvc.perform(put("/api/my-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMyOrder)))
            .andExpect(status().isOk());

        // Validate the MyOrder in the database
        List<MyOrder> myOrderList = myOrderRepository.findAll();
        assertThat(myOrderList).hasSize(databaseSizeBeforeUpdate);
        MyOrder testMyOrder = myOrderList.get(myOrderList.size() - 1);
        assertThat(testMyOrder.getCustId()).isEqualTo(UPDATED_CUST_ID);
        assertThat(testMyOrder.getProdCnt()).isEqualTo(UPDATED_PROD_CNT);
        assertThat(testMyOrder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingMyOrder() throws Exception {
        int databaseSizeBeforeUpdate = myOrderRepository.findAll().size();

        // Create the MyOrder

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMyOrderMockMvc.perform(put("/api/my-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(myOrder)))
            .andExpect(status().isCreated());

        // Validate the MyOrder in the database
        List<MyOrder> myOrderList = myOrderRepository.findAll();
        assertThat(myOrderList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMyOrder() throws Exception {
        // Initialize the database
        myOrderRepository.saveAndFlush(myOrder);
        int databaseSizeBeforeDelete = myOrderRepository.findAll().size();

        // Get the myOrder
        restMyOrderMockMvc.perform(delete("/api/my-orders/{id}", myOrder.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MyOrder> myOrderList = myOrderRepository.findAll();
        assertThat(myOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MyOrder.class);
    }
}
