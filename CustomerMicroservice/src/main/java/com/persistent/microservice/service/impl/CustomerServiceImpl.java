package com.persistent.microservice.service.impl;

import org.json.JSONException;
//import org.json.simple.JSONObject;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.persistent.microservice.domain.Customer;
import com.persistent.microservice.repository.CustomerRepository;
import com.persistent.microservice.service.CustomerService;
import com.persistent.microservice.utils.MicroserviceUtils;

/**
 * Service Implementation for managing Customer.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{
	
    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Save a customer.
     *
     * @param customer the entity to save
     * @return the persisted entity
     */
    @Override
    public Customer save(Customer customer) {
        log.debug("Request to save Customer : {}", customer);
        Customer result = customerRepository.save(customer);
        return result;
    }

    /**
     *  Get all the customers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAll(Pageable pageable) {
        log.debug("Request to get all Customers");
        Page<Customer> result = customerRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one customer by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Customer findOne(Long id) {
        log.debug("Request to get Customer : {}", id);
        Customer customer = customerRepository.findOne(id);
        return customer;
    }

    /**
     *  Delete the  customer by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        customerRepository.delete(id);
    }

	@Override
	public void creditBalance(JSONObject jsonObject) {
		System.out.println("Request to save Customer : {}"+ jsonObject);
		try{
			long lCustId =jsonObject.getLong ("customerId");
			double dAmount = jsonObject.getDouble("totalamount");
			
			Customer customer = this.findOne(lCustId);
			double dNewBalance = customer.getBalance() + dAmount;
			customer.setBalance(dNewBalance);
			this.save(customer);
			System.out.println("Request to save Customer : {}"+ customer.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void debitBalance(Customer customer, double dAmount) {
		System.out.println("Request to save Customer : {}"+ customer.toString() +" Amount " +dAmount );
		double dNewBalance = customer.getBalance() - dAmount;
		customer.setBalance(dNewBalance);
		this.save(customer);
		System.out.println("Request to save Customer : {}"+ customer.toString());
	}

	@Override
	public JSONObject checkBalance(JSONObject jsonObject) {
		JSONObject jSendMessage = jsonObject;
		try{
			
			long lCustId = jsonObject.getLong("customerId");
			double dAmount = jsonObject.getDouble("totalamount");
			
			Customer customer = this.findOne(lCustId);
			if(dAmount>customer.getBalance()){
				jSendMessage.put("message", MicroserviceUtils.CREDIT_CHECK_FAIL);
			}else{
				
				this.debitBalance(customer, dAmount);
				jSendMessage.put("message",  MicroserviceUtils.CREDIT_RESERVED);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return jSendMessage;
	}
}
