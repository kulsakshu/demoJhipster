package com.persistent.microservice.service;

//import org.json.simple.JSONObject;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.persistent.microservice.domain.Customer;

/**
 * Service Interface for managing Customer.
 */
public interface CustomerService {

    /**
     * Save a customer.
     *
     * @param customer the entity to save
     * @return the persisted entity
     */
    Customer save(Customer customer);

    /**
     *  Get all the customers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Customer> findAll(Pageable pageable);

    /**
     *  Get the "id" customer.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Customer findOne(Long id);

    /**
     *  Delete the "id" customer.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
    
    
    void creditBalance(JSONObject jsonObject);
    
    void debitBalance(Customer customer, double dAmount);
    
    JSONObject checkBalance(JSONObject jsonObject);
}
