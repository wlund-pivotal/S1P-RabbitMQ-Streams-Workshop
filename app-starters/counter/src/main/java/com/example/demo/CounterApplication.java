package com.example.demo;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.analytics.metrics.redis.RedisMetricRepository;
import org.springframework.analytics.rest.domain.Delta;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.EvaluationContext;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.messaging.Message;

@SpringBootApplication
@EnableConfigurationProperties(CounterProperties.class)
public class CounterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CounterApplication.class, args);
	}

	private static final Log logger = LogFactory.getLog(CounterApplication.class);

	@Autowired
	private RedisMetricRepository counterService;

	@Autowired
	private CounterProperties counterSinkProperties;

	@Autowired
	private BeanFactory beanFactory;

	private EvaluationContext evaluationContext;

	@PostConstruct
	public void init() {
		this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(this.beanFactory);
	}

	@Bean
	public Consumer<Message<?>> count() {
		return msg -> count(msg);
	}

	private void count(Message<?> message) {
		String name = computeMetricName(message);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Received: %s, about to increment counter named '%s'", message, name));
		}
		if (!name.startsWith("counter.")) {
			name = "counter." + name;
		}
		this.counterService.increment(new Delta<>(name, 1));
	}

	protected String computeMetricName(Message<?> message) {
		if (this.counterSinkProperties.getName() != null) {
			return this.counterSinkProperties.getName();
		}
		else {
			return this.counterSinkProperties.getNameExpression()
					.getValue(this.evaluationContext, message, CharSequence.class).toString();
		}
	}

}
