package com.example.demo;

import com.nimbusds.jose.JWSObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Filter to extract and print DPoP (Demonstrating Proof of Possession) context information.
 * This filter runs before authentication to capture DPoP proof JWT from the DPoP header.
 */
@Component
public class DPoPContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("\n======================================");
        System.out.println("=== DPoP Request Context ===");
        System.out.println("======================================");
        
        // 1. Print HTTP Method and URI
        System.out.println("\n1. HTTP Request Details:");
        System.out.println("   Method: " + request.getMethod());
        System.out.println("   URI: " + request.getRequestURI());
        System.out.println("   URL: " + request.getRequestURL());
        
        // 2. Extract and print Authorization header
        System.out.println("\n2. Authorization Header:");
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            System.out.println("   Full header: " + authHeader);
            if (authHeader.startsWith("DPoP ")) {
                System.out.println("   Scheme: DPoP (DPoP-bound token)");
                String accessToken = authHeader.substring(5);
                System.out.println("   Access Token (first 20 chars): " + 
                    (accessToken.length() > 20 ? accessToken.substring(0, 20) + "..." : accessToken));
            } else if (authHeader.startsWith("Bearer ")) {
                System.out.println("   Scheme: Bearer (standard bearer token)");
            } else {
                System.out.println("   Scheme: " + authHeader.split(" ")[0]);
            }
        } else {
            System.out.println("   No Authorization header present");
        }
        
        // 3. Extract and print DPoP header (DPoP Proof JWT)
        System.out.println("\n3. DPoP Header (DPoP Proof JWT):");
        String dPopHeader = request.getHeader("DPoP");
        if (dPopHeader != null) {
            System.out.println("   DPoP header present: Yes");
            System.out.println("   DPoP Proof JWT (first 50 chars): " + 
                (dPopHeader.length() > 50 ? dPopHeader.substring(0, 50) + "..." : dPopHeader));
            
            try {
                // Parse the DPoP proof JWT
                JWSObject jwsObject = JWSObject.parse(dPopHeader);
                
                // Print DPoP proof header
                System.out.println("\n   DPoP Proof Header:");
                Map<String, Object> header = jwsObject.getHeader().toJSONObject();
                header.forEach((key, value) -> {
                    if ("jwk".equals(key) && value instanceof Map) {
                        System.out.println("      " + key + ": (JWK public key present)");
                        @SuppressWarnings("unchecked")
                        Map<String, Object> jwk = (Map<String, Object>) value;
                        System.out.println("         kty: " + jwk.get("kty"));
                        System.out.println("         kid: " + jwk.get("kid"));
                        if (jwk.get("n") != null) {
                            String n = jwk.get("n").toString();
                            System.out.println("         n: " + (n.length() > 30 ? n.substring(0, 30) + "..." : n));
                        }
                    } else {
                        System.out.println("      " + key + ": " + value);
                    }
                });
                
                // Print DPoP proof payload/claims
                System.out.println("\n   DPoP Proof Claims:");
                Map<String, Object> payload = jwsObject.getPayload().toJSONObject();
                payload.forEach((key, value) -> 
                    System.out.println("      " + key + ": " + value)
                );
                
                // Explain the claims
                System.out.println("\n   DPoP Proof Claim Explanations:");
                if (payload.containsKey("htm")) {
                    System.out.println("      htm (HTTP Method): Binds proof to HTTP method");
                }
                if (payload.containsKey("htu")) {
                    System.out.println("      htu (HTTP URI): Binds proof to target URI");
                }
                if (payload.containsKey("ath")) {
                    System.out.println("      ath (Access Token Hash): SHA-256 hash of access token");
                }
                if (payload.containsKey("jti")) {
                    System.out.println("      jti (JWT ID): Unique identifier to prevent replay attacks");
                }
                if (payload.containsKey("iat")) {
                    System.out.println("      iat (Issued At): Timestamp when proof was created");
                }
                
            } catch (Exception e) {
                System.out.println("   Failed to parse DPoP proof JWT: " + e.getMessage());
            }
        } else {
            System.out.println("   No DPoP header present");
            System.out.println("   -> This is NOT a DPoP-protected request");
        }
        
        // 4. Print other relevant headers
        System.out.println("\n4. Other Request Headers:");
        System.out.println("   Content-Type: " + request.getHeader("Content-Type"));
        System.out.println("   User-Agent: " + request.getHeader("User-Agent"));
        
        System.out.println("\n======================================\n");
        
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
