package com.molchanova.course.security;

import com.molchanova.course.logging.SecurityAuditLogger;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    @Value("${app.jwtSecret:mySecretKey}")
    private String jwtSecret;
    
    @Value("${app.jwtExpirationMs:86400000}")
    private int jwtExpirationMs;
    
    @Autowired
    private SecurityAuditLogger securityAuditLogger;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        
        String token = Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
        
        logger.debug("JWT token generated for user: {}", maskUsername(userPrincipal.getUsername()));
        return token;
    }
    
    public String generateTokenFromUsername(String username) {
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
        
        logger.debug("JWT token generated from username: {}", maskUsername(username));
        return token;
    }
    
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            securityAuditLogger.logTokenValidationFailure(authToken, "Malformed JWT token");
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
            securityAuditLogger.logTokenValidationFailure(authToken, "Expired JWT token");
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT token is unsupported: {}", e.getMessage());
            securityAuditLogger.logTokenValidationFailure(authToken, "Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty: {}", e.getMessage());
            securityAuditLogger.logTokenValidationFailure(authToken, "Empty JWT claims");
        } catch (Exception e) {
            logger.error("JWT token validation failed: {}", e.getMessage());
            securityAuditLogger.logTokenValidationFailure(authToken, "General validation failure: " + e.getMessage());
        }
        return false;
    }
    
    private String maskUsername(String username) {
        if (username == null || username.length() <= 2) {
            return "***";
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
} 