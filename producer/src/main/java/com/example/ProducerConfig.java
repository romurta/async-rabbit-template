package com.example;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Zoltan Altfatter
 */
@Configuration
public class ProducerConfig {

    static final String FIBO_CALCULATOR_EXCHANGE_NAME = "app.fibonacci.calculator";
    static final String FIBO_CALCULATOR_REQUEST_QUEUE_NAME = "app.fibonacci.request";
    static final String FIBO_CALCULATOR_REPLY_QUEUE_NAME = "app.fibonacci.reply";
    static final String FIBO_CALCULATOR_ROUTING_KEY_NAME = "fibo";

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @PostConstruct
    public void postConstructor(){
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> System.out.println(correlationData + " - " + ack));
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(FIBO_CALCULATOR_EXCHANGE_NAME);
    }

    @Bean
    Queue requestQueue() {
        return QueueBuilder.durable(FIBO_CALCULATOR_REQUEST_QUEUE_NAME).build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(requestQueue()).to(exchange()).with(FIBO_CALCULATOR_ROUTING_KEY_NAME);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
