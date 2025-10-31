package org.mekluppie.restapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Configuration for JWT decoder that supports DPoP-bound access tokens.
 * 
 * Note: Spring Security 6.3+ has built-in DPoP support. For earlier versions,
 * this configuration enables JWT decoding while the DPoPContextFilter handles
 * DPoP proof extraction and logging.
 */
//@Configuration
public class DPoPJwtDecoderConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:#{null}}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:#{null}}")
    private String jwkSetUri;

    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder() {
        System.out.println("\n=== Configuring JWT Decoder for DPoP ===");
        
        if (jwkSetUri != null && !jwkSetUri.isEmpty()) {
            System.out.println("Using JWK Set URI: " + jwkSetUri);
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
            System.out.println("JWT Decoder configured with JWK Set URI");
            return decoder;
        } else if (issuerUri != null && !issuerUri.isEmpty()) {
            System.out.println("Using Issuer URI: " + issuerUri);
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
            System.out.println("JWT Decoder configured with Issuer URI");
            return decoder;
        } else {
            System.out.println("WARNING: No issuer-uri or jwk-set-uri configured!");
            System.out.println("Please set spring.security.oauth2.resourceserver.jwt.issuer-uri or");
            System.out.println("spring.security.oauth2.resourceserver.jwt.jwk-set-uri in application.yaml");
            // Return a minimal decoder - this will fail at runtime if tokens are validated
            throw new IllegalStateException(
                "JWT decoder requires either 'spring.security.oauth2.resourceserver.jwt.issuer-uri' " +
                "or 'spring.security.oauth2.resourceserver.jwt.jwk-set-uri' to be configured"
            );
        }
    }
}
