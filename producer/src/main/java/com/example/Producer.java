package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static com.example.ProducerConfig.FIBO_CALCULATOR_EXCHANGE_NAME;
import static com.example.ProducerConfig.FIBO_CALCULATOR_ROUTING_KEY_NAME;

/**
 * @author Zoltan Altfatter
 */
@Component
@Slf4j
public class Producer {

    private RabbitTemplate rabbitTemplate;

    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 1000L)
    public void send() {
        int number = new Random().nextInt(45);

        FiboCalcRequest request = new FiboCalcRequest(number);

        CorrelationData correlationData = new CorrelationData("THIAGO"+UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend(FIBO_CALCULATOR_EXCHANGE_NAME, FIBO_CALCULATOR_ROUTING_KEY_NAME, request, correlationData);
        log.info("Thread: '{}' calc fibonacci for number '{}'", Thread.currentThread().getName(), number);

    }

}
