package com.molchanova.course.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String category;

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;
}
