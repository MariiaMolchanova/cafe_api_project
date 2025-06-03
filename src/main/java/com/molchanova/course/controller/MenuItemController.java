package com.molchanova.course.controller;

import com.molchanova.course.entity.MenuItem;
import com.molchanova.course.repository.MenuItemRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuItemController {
    private final MenuItemRepo menuItemRepo;

    public MenuItemController(MenuItemRepo menuItemRepo) {
        this.menuItemRepo = menuItemRepo;
    }

    @GetMapping("/{cafeId}")
    public List<MenuItem> getMenuByCafe(@PathVariable Long cafeId) {
        return menuItemRepo.findByCafeId(cafeId);
    }

    @PostMapping
    public MenuItem createMenuItem(@RequestBody MenuItem menuItem) {
        return menuItemRepo.save(menuItem);
    }
}