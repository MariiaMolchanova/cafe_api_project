package com.molchanova.course.controller;

import com.molchanova.course.entity.Cafe;
import com.molchanova.course.repository.CafeRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cafes")
public class CafeController {
    private final CafeRepo cafeRepo;

    public CafeController(CafeRepo cafeRepo) {
        this.cafeRepo = cafeRepo;
    }

    @GetMapping
    public List<Cafe> getAllCafes() {
        return cafeRepo.findAll();
    }

    @PostMapping
    public Cafe createCafe(@RequestBody Cafe cafe) {
        return cafeRepo.save(cafe);
    }
}
