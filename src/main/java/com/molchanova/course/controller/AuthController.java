package com.molchanova.course.controller;

import com.molchanova.course.data.JwtResponseDTO;
import com.molchanova.course.data.LoginRequestDTO;
import com.molchanova.course.entity.User;
import com.molchanova.course.logging.SecurityAuditLogger;
import com.molchanova.course.repository.UserRepo;
import com.molchanova.course.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    SecurityAuditLogger securityAuditLogger;
    
    @GetMapping("/api/test/users")
    @ResponseBody
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepo.findAll();
        logger.info("User count request - Total users: {}", users.size());
        return ResponseEntity.ok("Total users: " + users.size() + ", Users: " + 
            users.stream().map(User::getUsername).collect(Collectors.toList()));
    }
    
  
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error, 
                               @RequestParam(required = false) String logout,
                               Model model,
                               HttpServletRequest request) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
            logger.warn("Login page accessed with error parameter from IP: {}", getClientIp(request));
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
            logger.info("Login page accessed after logout from IP: {}", getClientIp(request));
        }
        return "login";
    }
    

    @PostMapping("/api/auth/signin")
    @ResponseBody
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest,
                                            HttpServletRequest request) {
        try {
            logger.debug("API login attempt for user: {}", maskUsername(loginRequest.getUsername()));
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            User userDetails = (User) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
            
         
            securityAuditLogger.logLoginSuccess(userDetails.getUsername(), request);
            securityAuditLogger.logTokenGeneration(userDetails.getUsername());
            
            logger.info("API login successful for user: {}", maskUsername(userDetails.getUsername()));
            
            return ResponseEntity.ok(new JwtResponseDTO(jwt, userDetails.getId(), userDetails.getUsername(), roles));
        } catch (Exception e) {
    
            securityAuditLogger.logLoginFailure(loginRequest.getUsername(), e.getMessage(), request);
            logger.warn("API login failed for user: {} - Reason: {}", 
                maskUsername(loginRequest.getUsername()), e.getMessage());
            
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Invalid username or password!"));
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
