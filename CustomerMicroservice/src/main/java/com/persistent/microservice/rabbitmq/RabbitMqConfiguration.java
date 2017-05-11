package com.persistent.microservice.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.persistent.microservice.repository.RabbitMQProperties;

@Configuration
public class RabbitMqConfiguration {
	
	@Autowired
	private RabbitMQProperties properties ;
	
	
	public final static String ORDER_TO_CUSTOMER_QUEUE = "CustomerCheck";
	public final static String CUSTOMER_To_ORDER_QUEUE = "CustomerToOrder";
	//public final static String FAILURE_QUEUE = "InventoryCheckFailure";
	
	
	/*@Bean
    public ConnectionFactory connectionFactory()
    {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getServerName());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        return connectionFactory;
    }*/
	
	@Bean
    public ConnectionFactory connectionFactory()
    {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("brl05798");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        return connectionFactory;
    }

	
	@Bean
	DirectExchange exchange() {
		return new DirectExchange("catlog.exchange");
	}
	
	@Bean
    public Queue orderQueue() {
        return new Queue(ORDER_TO_CUSTOMER_QUEUE);
    }
	@Bean
    public Queue reservedQueue() {
        return new Queue(CUSTOMER_To_ORDER_QUEUE);
    }
	/*
	@Bean
    public Queue failureQueue() {
        return new Queue(FAILURE_QUEUE);
    }*/
	
	@Bean
	@Qualifier()
	Binding orderBinding(Queue orderQueue, DirectExchange exchange) {
		return BindingBuilder.bind(orderQueue).to(exchange).with(ORDER_TO_CUSTOMER_QUEUE);
	}
	
	@Bean
	Binding reservedBinding(Queue reservedQueue, DirectExchange exchange) {
		return BindingBuilder.bind(reservedQueue).to(exchange).with(CUSTOMER_To_ORDER_QUEUE);
	}
	
	/*@Bean
	Binding failureBinding(Queue failureQueue, DirectExchange exchange) {
		return BindingBuilder.bind(failureQueue).to(exchange).with(FAILURE_QUEUE);
	}*/
	
	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
	MessageListenerAdapter listenerAdapter) {
        System.out.println(connectionFactory.getHost());
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(ORDER_TO_CUSTOMER_QUEUE);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(ManageEvent manageEvent) {
		MessageListenerAdapter  adapter = new MessageListenerAdapter(manageEvent , "receiveMessageFromOrderQueue");
		return adapter;
	}
	
	@Bean
    public RabbitTemplate rabbitTemplate()
    {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setRoutingKey(CUSTOMER_To_ORDER_QUEUE);
        template.setQueue(CUSTOMER_To_ORDER_QUEUE);
        return template;
    }


}
