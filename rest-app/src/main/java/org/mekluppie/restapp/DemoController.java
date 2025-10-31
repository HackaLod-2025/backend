package org.mekluppie.restapp;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/")
    public HelloMessage sayHello(Authentication authentication) {
        System.out.println("=== DPoP Context Information ===");
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            // Print JWT token information
            System.out.println("\n1. JWT Token Information:");
            System.out.println("   Subject: " + jwt.getSubject());
            System.out.println("   Issuer: " + jwt.getIssuer());
            System.out.println("   Issued At: " + jwt.getIssuedAt());
            System.out.println("   Expires At: " + jwt.getExpiresAt());
            
            // Print DPoP confirmation claim (cnf)
            System.out.println("\n2. DPoP Confirmation Claim (cnf):");
            Map<String, Object> cnf = jwt.getClaim("cnf");
            if (cnf != null) {
                System.out.println("   CNF Claim found: " + cnf);
                Object jkt = cnf.get("jkt");
                if (jkt != null) {
                    System.out.println("   JWK Thumbprint (jkt): " + jkt);
                    System.out.println("   -> This is the SHA-256 thumbprint of the DPoP public key");
                } else {
                    System.out.println("   No 'jkt' (JWK Thumbprint) found in cnf claim");
                }
            } else {
                System.out.println("   No 'cnf' claim found - this might not be a DPoP-bound token");
            }
            
            // Print all JWT claims
            System.out.println("\n3. All JWT Claims:");
            jwt.getClaims().forEach((key, value) -> 
                System.out.println("   " + key + ": " + value)
            );
            
            // Print JWT headers
            System.out.println("\n4. JWT Headers:");
            jwt.getHeaders().forEach((key, value) -> 
                System.out.println("   " + key + ": " + value)
            );
            
            // Print authentication details
            System.out.println("\n5. Authentication Details:");
            System.out.println("   Principal: " + jwtAuth.getName());
            System.out.println("   Authorities: " + jwtAuth.getAuthorities());
            
        } else {
            System.out.println("Authentication is not a JWT token: " + 
                (authentication != null ? authentication.getClass().getName() : "null"));
        }
        
        System.out.println("\n=== End DPoP Context Information ===\n");
        
        return new HelloMessage("Hello, DPoP World!");
    }

    public record HelloMessage(String message) { }
}
