package com.molchanova.course.controller;

import com.molchanova.course.data.OrderRequestDTO;
import com.molchanova.course.entity.Order;
import com.molchanova.course.repository.OrderRepo;
import com.molchanova.course.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepo orderRepo;

    public OrderController(OrderService orderService, OrderRepo orderRepo) {
        this.orderService = orderService;
        this.orderRepo = orderRepo;
    }

    // Web page endpoint
    @GetMapping("/view")
    public String ordersPage() {
        return "orders";
    }

    // REST API endpoints
    @PostMapping
    @ResponseBody
    public Order createOrder(@RequestBody OrderRequestDTO orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @GetMapping("/by-customer")
    @ResponseBody
    public List<Order> getOrdersByCustomer(@RequestParam String customerName) {
        return orderService.findByCustomerName(customerName);
    }
    
    @GetMapping
    @ResponseBody
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }
}
