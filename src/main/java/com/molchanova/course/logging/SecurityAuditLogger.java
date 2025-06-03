package com.molchanova.course.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SecurityAuditLogger {
    
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_AUDIT");
    
    public enum AuditEvent {
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        LOGOUT,
        REGISTRATION_SUCCESS,
        REGISTRATION_FAILURE,
        TOKEN_GENERATION,
        TOKEN_VALIDATION_FAILURE,
        UNAUTHORIZED_ACCESS,
        PASSWORD_CHANGE,
        ACCOUNT_LOCKED
    }
    
    public void logSecurityEvent(AuditEvent event, String username, String details, HttpServletRequest request) {
        try {
            // Set MDC context for structured logging
            MDC.put("event_type", event.name());
            MDC.put("username", maskSensitiveData(username));
            MDC.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            MDC.put("ip_address", getClientIpAddress(request));
            MDC.put("user_agent", request != null ? request.getHeader("User-Agent") : "unknown");
            MDC.put("session_id", request != null ? request.getRequestedSessionId() : "none");
            MDC.put("details", details);
            
            securityLogger.info("Security Event: {} for user: {} from IP: {}", 
                event.name(), 
                maskSensitiveData(username), 
                getClientIpAddress(request));
                
        } finally {
            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }
    
    public void logSecurityEvent(AuditEvent event, String username, String details) {
        logSecurityEvent(event, username, details, null);
    }
    
    public void logLoginSuccess(String username, HttpServletRequest request) {
        logSecurityEvent(AuditEvent.LOGIN_SUCCESS, username, "User successfully authenticated", request);
    }
    
    public void logLoginFailure(String username, String reason, HttpServletRequest request) {
        logSecurityEvent(AuditEvent.LOGIN_FAILURE, username, "Authentication failed: " + reason, request);
    }
    
    public void logLogout(String username, HttpServletRequest request) {
        logSecurityEvent(AuditEvent.LOGOUT, username, "User logged out", request);
    }
    
    public void logRegistration(String username, boolean success, String details, HttpServletRequest request) {
        AuditEvent event = success ? AuditEvent.REGISTRATION_SUCCESS : AuditEvent.REGISTRATION_FAILURE;
        logSecurityEvent(event, username, details, request);
    }
    
    public void logTokenGeneration(String username) {
        logSecurityEvent(AuditEvent.TOKEN_GENERATION, username, "JWT token generated");
    }
    
    public void logTokenValidationFailure(String token, String reason) {
        logSecurityEvent(AuditEvent.TOKEN_VALIDATION_FAILURE, "unknown", 
            "Token validation failed: " + reason + " for token: " + maskToken(token));
    }
    
    public void logUnauthorizedAccess(String path, HttpServletRequest request) {
        logSecurityEvent(AuditEvent.UNAUTHORIZED_ACCESS, "unknown", 
            "Unauthorized access attempt to: " + path, request);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return "unknown";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 2) {
            return "***";
        }
        return data.charAt(0) + "***" + data.charAt(data.length() - 1);
    }
    
    private String maskToken(String token) {
        if (token == null || token.length() <= 10) {
            return "***MASKED***";
        }
        return token.substring(0, 5) + "***MASKED***" + token.substring(token.length() - 5);
    }
} 