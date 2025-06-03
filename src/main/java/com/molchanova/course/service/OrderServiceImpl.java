package com.molchanova.course.service;

import com.molchanova.course.data.OrderRequestDTO;
import com.molchanova.course.entity.Cafe;
import com.molchanova.course.entity.MenuItem;
import com.molchanova.course.entity.Order;
import com.molchanova.course.repository.CafeRepo;
import com.molchanova.course.repository.MenuItemRepo;
import com.molchanova.course.repository.OrderRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final CafeRepo cafeRepo;
    private final MenuItemRepo menuItemRepo;

    public OrderServiceImpl(OrderRepo orderRepo, CafeRepo cafeRepo, MenuItemRepo menuItemRepo) {
        this.orderRepo = orderRepo;
        this.cafeRepo = cafeRepo;
        this.menuItemRepo = menuItemRepo;
    }

    @Override
    public Order createOrder(OrderRequestDTO orderRequest) {
        Order order = new Order();
        order.setCustomerName(orderRequest.getCustomerName());
        
        // Set cafe
        Cafe cafe = cafeRepo.findById(orderRequest.getCafeId())
                .orElseThrow(() -> new RuntimeException("Cafe not found"));
        order.setCafe(cafe);
        
        // Set menu items
        List<MenuItem> menuItems = menuItemRepo.findAllById(orderRequest.getMenuItemIds());
        order.setItems(menuItems);
        
        // Calculate total amount
        double totalAmount = menuItems.stream()
                .mapToDouble(MenuItem::getPrice)
                .sum();
        order.setTotalAmount(totalAmount);
        
        return orderRepo.save(order);
    }

    @Override
    public List<Order> findByCustomerName(String customerName) {
        return orderRepo.findByCustomerName(customerName);
    }
} 