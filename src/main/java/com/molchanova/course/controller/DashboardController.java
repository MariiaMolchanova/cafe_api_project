package com.molchanova.course.controller;

import com.molchanova.course.entity.Cafe;
import com.molchanova.course.entity.MenuItem;
import com.molchanova.course.repository.CafeRepo;
import com.molchanova.course.repository.MenuItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    
    @Autowired
    private CafeRepo cafeRepo;
    
    @Autowired
    private MenuItemRepo menuItemRepo;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            
            model.addAttribute("username", authentication.getName());
            
           
            List<Cafe> cafes = cafeRepo.findAll();
            List<MenuItem> menuItems = menuItemRepo.findAll();
            
            model.addAttribute("cafes", cafes);
            model.addAttribute("menuItems", menuItems);
            
            return "dashboard";
        }
        
        return "redirect:/login";
    }
} 
