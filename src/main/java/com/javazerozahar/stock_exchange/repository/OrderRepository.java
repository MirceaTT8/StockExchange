package com.javazerozahar.stock_exchange.repository;

import com.javazerozahar.stock_exchange.model.entity.Order;
import com.javazerozahar.stock_exchange.model.entity.Stock;
import com.javazerozahar.stock_exchange.utils.Lockable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends Lockable<Long>, Resetable {
    Order save(Order order);

    Optional<Order> findById(Long orderId);

    void remove(Order order);

    List<Order> findByBoughtStock(Stock stock);

    List<Order> findBySoldStock(Stock stock);

    List<Order> findAll();
}
