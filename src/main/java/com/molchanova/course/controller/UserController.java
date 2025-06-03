package com.molchanova.course.controller;

import com.molchanova.course.data.UserDataRequestDTO;
import com.molchanova.course.entity.User;
import com.molchanova.course.logging.SecurityAuditLogger;
import com.molchanova.course.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final SecurityAuditLogger securityAuditLogger;
    
    public UserController(UserRepo userRepo, PasswordEncoder passwordEncoder, SecurityAuditLogger securityAuditLogger) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.securityAuditLogger = securityAuditLogger;
    }
    
    // Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(HttpServletRequest request) {
        logger.debug("Registration form accessed from IP: {}", getClientIp(request));
        return "register";
    }
    
    // Handle form submission
    @PostMapping("/register")
    public String handleRegistration(@RequestParam String username, 
                                   @RequestParam String password,
                                   @RequestParam String confirmPassword,
                                   HttpServletRequest request) {
        
        logger.debug("Registration form submission for user: {}", maskUsername(username));
        
        if (!password.equals(confirmPassword)) {
            securityAuditLogger.logRegistration(username, false, "Password confirmation mismatch", request);
            logger.warn("Registration failed for user: {} - Password confirmation mismatch", maskUsername(username));
            return "redirect:/register?error=passwords_dont_match";
        }
        
        if (userRepo.findByUsername(username).isPresent()) {
            securityAuditLogger.logRegistration(username, false, "Username already exists", request);
            logger.warn("Registration failed for user: {} - Username already exists", maskUsername(username));
            return "redirect:/register?error=username_exists";
        }
        
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            userRepo.save(user);
            
            securityAuditLogger.logRegistration(username, true, "User registered successfully via web form", request);
            logger.info("User registered successfully via web form: {}", maskUsername(username));
            
            return "redirect:/register?success=true";
        } catch (Exception e) {
            securityAuditLogger.logRegistration(username, false, "Database error: " + e.getMessage(), request);
            logger.error("Registration failed for user: {} - Database error: {}", maskUsername(username), e.getMessage());
            return "redirect:/register?error=registration_failed";
        }
    }
    
    // REST API endpoint for registration
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@RequestBody UserDataRequestDTO userRequest,
                                        HttpServletRequest request) {
        
        logger.debug("API registration attempt for user: {}", maskUsername(userRequest.getUsername()));
        
        if (userRepo.findByUsername(userRequest.getUsername()).isPresent()) {
            securityAuditLogger.logRegistration(userRequest.getUsername(), false, "Username already exists via API", request);
            logger.warn("API registration failed for user: {} - Username already exists", maskUsername(userRequest.getUsername()));
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Username is already taken!"));
        }
        
        try {
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            
            userRepo.save(user);
            
            securityAuditLogger.logRegistration(userRequest.getUsername(), true, "User registered successfully via API", request);
            logger.info("User registered successfully via API: {}", maskUsername(userRequest.getUsername()));
            
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (Exception e) {
            securityAuditLogger.logRegistration(userRequest.getUsername(), false, "Database error via API: " + e.getMessage(), request);
            logger.error("API registration failed for user: {} - Database error: {}", maskUsername(userRequest.getUsername()), e.getMessage());
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Registration failed!"));
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private String maskUsername(String username) {
        if (username == null || username.length() <= 2) {
            return "***";
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
    
    // Simple response class for REST API
    public static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
} 