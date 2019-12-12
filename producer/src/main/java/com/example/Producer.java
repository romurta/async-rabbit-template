package com.example;


import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.example.ProducerConfig.FIBO_CALCULATOR_EXCHANGE_NAME;
import static com.example.ProducerConfig.FIBO_CALCULATOR_ROUTING_KEY_NAME;

/**
 * @author Zoltan Altfatter
 */
@Component
@Slf4j
public class Producer {

    private AsyncRabbitTemplate asyncRabbitTemplate;

    public Producer(AsyncRabbitTemplate asyncRabbitTemplate) {
        this.asyncRabbitTemplate = asyncRabbitTemplate;
    }

    @Scheduled(fixedDelay = 1000L)
    public void send() {
        int number = new Random().nextInt(45);

        FiboCalcRequest request = new FiboCalcRequest(number);

        CorrelationData data = new CorrelationData(UUID.randomUUID().toString());

        AsyncRabbitTemplate.RabbitConverterFuture<FiboCalcResponse> future =
                asyncRabbitTemplate.convertSendAndReceive(FIBO_CALCULATOR_EXCHANGE_NAME, FIBO_CALCULATOR_ROUTING_KEY_NAME, request);
        log.info("Thread: '{}' calc fibonacci for number '{}'", Thread.currentThread().getName(), number);

        future.addCallback(new ListenableFutureCallback<FiboCalcResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("-----------ERROR-----------");
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(FiboCalcResponse result) {
                log.info("SUCCESS Thread: '{}' result: '{}'", Thread.currentThread().getName(), result);
            }
        });

       ListenableFuture<Boolean> confirmFuture = future.getConfirm();
       confirmFuture.addCallback(new ListenableFutureCallback<Boolean>() {
           @Override
           public void onFailure(Throwable throwable) {
               log.error("-----------ERROR CONFIRM-----------");
               throwable.printStackTrace();
           }

           @Override
           public void onSuccess(Boolean result) {
               log.info("SUCCESS CONFIRMThread: '{}' result: '{}'", Thread.currentThread().getName(), result);
           }
       });
    }
}
