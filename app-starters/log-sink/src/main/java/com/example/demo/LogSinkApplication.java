package com.example.demo;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@SpringBootApplication
@EnableConfigurationProperties(LogSinkProperties.class)
public class LogSinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogSinkApplication.class, args);
	}

	@Bean
	public Consumer<Message<?>> log(IntegrationFlow loggingFlow) {
		MessageChannel channel = loggingFlow.getInputChannel();
		return msg -> channel.send(msg);
	}

	@Bean
	IntegrationFlow loggingFlow(LogSinkProperties properties) {
		return f -> f.transform(Transformers.objectToString())
				.log(properties.getLevel(), properties.getName(), properties.getExpression());
	}

}
