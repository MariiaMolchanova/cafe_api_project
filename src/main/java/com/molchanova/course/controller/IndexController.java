package com.molchanova.course.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    
    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        logger.debug("Index page access - Authentication: {}", authentication != null ? authentication.getName() : "null");
        logger.debug("Is authenticated: {}", authentication != null ? authentication.isAuthenticated() : "null");
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
            logger.info("Authenticated user accessing home page: {}", maskUsername(authentication.getName()));
        } else {
            model.addAttribute("isAuthenticated", false);
            logger.debug("Anonymous user accessing home page");
        }
        
        return "index";
    }
    
    private String maskUsername(String username) {
        if (username == null || username.length() <= 2) {
            return "***";
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
} 