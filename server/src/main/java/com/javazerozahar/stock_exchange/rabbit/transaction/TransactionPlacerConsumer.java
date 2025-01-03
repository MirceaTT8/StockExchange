package com.javazerozahar.stock_exchange.rabbit.transaction;

import com.google.gson.Gson;
import com.javazerozahar.stock_exchange.converters.OrderConverter;
import com.javazerozahar.stock_exchange.rabbit.general.MessageTracker;
import com.javazerozahar.stock_exchange.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionPlacerConsumer {

    @Value("${rabbitmq.queue.transaction}")
    private String queueName;

    private final TransactionService transactionService;
    private final OrderConverter orderConverter;
    private final MessageTracker messageTracker;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "${rabbitmq.queue.transaction}", durable = "true"),
            exchange = @Exchange(name = "transaction-exchange"),
            key = "transaction-routing-key"
    ))
    public void receiveTransaction(String message) {
        processTransaction(new Gson().fromJson(message, TransactionRabbitMqDTO.class));
    }

    private void processTransaction(TransactionRabbitMqDTO transaction) {

        try {
            transactionService.createTransaction(
                    orderConverter.toOrder(transaction.getOrder()),
                    orderConverter.toOrder(transaction.getMatchedOrder()),
                    transaction.getMatchedQuantity()
            );

            if (log.isInfoEnabled()) {
                log.info("Processed transaction for orders: {} {}",
                        transaction.getOrder(), transaction.getMatchedOrder());
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Unable to process transaction for orders {} {} ",
                        transaction.getOrder(), transaction.getMatchedOrder(), e);
            }
        } finally {
            messageTracker.decrement(queueName);
        }
    }

}
