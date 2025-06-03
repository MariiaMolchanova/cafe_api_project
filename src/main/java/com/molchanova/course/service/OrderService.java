package com.molchanova.course.service;

import com.molchanova.course.data.OrderRequestDTO;
import com.molchanova.course.entity.Order;
import java.util.List;
 
public interface OrderService {
    Order createOrder(OrderRequestDTO orderRequest);
    List<Order> findByCustomerName(String customerName);
} 