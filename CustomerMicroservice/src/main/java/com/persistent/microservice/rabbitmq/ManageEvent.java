package com.persistent.microservice.rabbitmq;

import org.json.JSONException;
//import org.json.simple.JSONObject;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.persistent.microservice.domain.Customer;
import com.persistent.microservice.service.CustomerService;
import com.persistent.microservice.utils.MicroserviceUtils;

@Component
public class ManageEvent {
	
	  private final RabbitTemplate rabbitTemplate;
	  
	  private final CustomerService service;
	  
	  
	
	public ManageEvent(RabbitTemplate rabbitTemplate , CustomerService service) {
		this.rabbitTemplate = rabbitTemplate;
		this.service = service;
	}



	public void receiveMessageFromOrderQueue(String jsonObject){
		
		
		
		System.out.println("############################################################");
		
		System.out.println("Message Received from ORDER_QUEUE- " + jsonObject);
		
		String sEventType ="";
		String sInventryCheck = "";
		String sCustomerCheck = "";
		try {
			JSONObject jSendMessage = new JSONObject(jsonObject);
			sEventType = jSendMessage.getString("eventType");
			
			if(sEventType.equalsIgnoreCase(MicroserviceUtils.CREATE_ORDER)){
				
				jSendMessage  = service.checkBalance(jSendMessage);
				
				rabbitTemplate.convertAndSend(RabbitMqConfiguration.CUSTOMER_To_ORDER_QUEUE, jSendMessage.toString());
				
				System.out.println("Send event to order  - " + jSendMessage);
				
			}else if(sEventType.equalsIgnoreCase(MicroserviceUtils.ORDER_FAILURE)){
				sInventryCheck = jSendMessage.getString("inventoryCheck");
				sCustomerCheck = jSendMessage.getString("customerCheck");
				if(sInventryCheck.equalsIgnoreCase("failure") && sCustomerCheck.equalsIgnoreCase("CreditReserved")){
					System.out.println("Recived order failure event .. ");
					service.creditBalance(jSendMessage);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println("############################################################");
		
	}
	
	

}
