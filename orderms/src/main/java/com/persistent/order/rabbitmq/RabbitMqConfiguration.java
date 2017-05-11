package com.persistent.order.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {
	
	public final static String ORDER_QUEUE = "CreateOrder";
	public final static String RESERVED_QUEUE = "InventoryReserved";
	public final static String FAILURE_QUEUE = "InventoryCheckFailure";
	
	
	@Bean
    public ConnectionFactory connectionFactory()
    {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("brl05798");
        //connectionFactory.setVirtualHost("http");
        //connectionFactory.setPort(15672);
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
        return new Queue(ORDER_QUEUE);
    }
	@Bean
    public Queue reservedQueue() {
        return new Queue(RESERVED_QUEUE);
    }
	@Bean
    public Queue failureQueue() {
        return new Queue(FAILURE_QUEUE);
    }
	
	@Bean
	@Qualifier()
	Binding orderBinding(Queue orderQueue, DirectExchange exchange) {
		return BindingBuilder.bind(orderQueue).to(exchange).with(ORDER_QUEUE);
	}
	
	@Bean
	Binding reservedBinding(Queue reservedQueue, DirectExchange exchange) {
		return BindingBuilder.bind(reservedQueue).to(exchange).with(RESERVED_QUEUE);
	}
	
	@Bean
	Binding failureBinding(Queue failureQueue, DirectExchange exchange) {
		return BindingBuilder.bind(failureQueue).to(exchange).with(FAILURE_QUEUE);
	}
	
	/*@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
	MessageListenerAdapter listenerAdapter) {
		//connectionFactory = connectionFactory();
        System.out.println(connectionFactory.getHost());
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(ORDER_QUEUE);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Consumer consumer) {
		return new MessageListenerAdapter(consumer, "receiveMessage");
	}*/
	
	@Bean
    public RabbitTemplate rabbitTemplate()
    {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setRoutingKey(ORDER_QUEUE);
        template.setQueue(ORDER_QUEUE);
        //template.setMessageConverter((MessageConverter) jsonMessageConverter());
        return template;
    }
	
	/*@Profile("sender")
    @Bean
    public Producer successSender() {
        return new Producer();
    }
	
	@Profile("sender")
    @Bean
    public Producer2 failureSender() {
        return new Producer2();
    }*/

}
