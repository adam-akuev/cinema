package com.akuev.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthDebugController {
    
    @GetMapping("/api/debug/auth-simple")
    public Map<String, Object> debugAuthSimple(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        
        if (authentication != null) {
            result.put("name", authentication.getName());
            result.put("authorities", authentication.getAuthorities().toString());
            result.put("authenticated", authentication.isAuthenticated());
            result.put("class", authentication.getClass().getSimpleName());
            
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                result.put("jwt_issuer", jwt.getIssuer().toString());
                result.put("jwt_subject", jwt.getSubject());
            }
        } else {
            result.put("error", "Authentication is null");
        }
        
        return result;
    }
    
    @GetMapping("/api/debug/auth-details")
    @RolesAllowed({"USER", "ADMIN"}) // Этот должен требовать роли
    public Map<String, Object> debugAuthDetails(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("username", authentication.getName());
        result.put("authorities", authentication.getAuthorities());
        result.put("authenticated", authentication.isAuthenticated());
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Покажем сырые claims из токена
            result.put("resource_access", jwt.getClaim("resource_access"));
            result.put("realm_access", jwt.getClaim("realm_access"));
            result.put("all_claims", jwt.getClaims());
        }
        
        return result;
    }
}