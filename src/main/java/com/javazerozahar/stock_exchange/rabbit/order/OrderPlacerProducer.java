package com.javazerozahar.stock_exchange.rabbit.order;

import com.google.gson.Gson;
import com.javazerozahar.stock_exchange.model.entity.Order;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class OrderPlacerProducer {
    private static final String QUEUE_NAME = "order-queue";
    private final ConnectionFactory connectionFactory;

    public OrderPlacerProducer() {
        this.connectionFactory = new ConnectionFactory();
        connectionFactory.setPort(5672);
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
    }

    public void sendOrder(Order order) {
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            String message = new Gson().toJson(order);

            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Order sent to queue: " + message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}