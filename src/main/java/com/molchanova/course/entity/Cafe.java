package com.molchanova.course.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Cafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String city;
}

