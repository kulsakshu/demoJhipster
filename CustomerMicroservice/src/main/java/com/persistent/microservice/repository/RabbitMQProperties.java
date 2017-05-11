package com.persistent.microservice.repository;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
@PropertySource("rabbitmq-config.properties")
@EnableConfigurationProperties
public class RabbitMQProperties {
	
	//@Value("${rabbitmq.serverName}")
		private String serverName;
		
		//@Value("${rabbitmq.username}")
		private String username;
		
		
		//@Value("${rabbitmq.password}")
		private String password;
	
	/*public RabbitMQProperties(String serverName, String username, String password) {
		this.serverName = serverName;
		this.username = username;
		this.password = password;
		System.out.println(toString());
	}
	*/
	
	@Override
	public String toString() {
		return "RabbitMQProperties [serverName=" + serverName + ", username=" + username + ", password=" + password
				+ "]";
	}

	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
