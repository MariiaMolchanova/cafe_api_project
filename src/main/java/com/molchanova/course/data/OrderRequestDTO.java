package com.molchanova.course.data;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    private Long cafeId;
    private String customerName;
    private List<Long> menuItemIds;
} 