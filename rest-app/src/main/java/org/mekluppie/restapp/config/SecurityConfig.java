package org.mekluppie.restapp.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import com.nimbusds.jose.JOSEObjectType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final DPoPDebugFilter dpopDebugFilter;
    
    public SecurityConfig(DPoPDebugFilter dpopDebugFilter) {
        this.dpopDebugFilter = dpopDebugFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(dpopDebugFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        // Create JWT processor with ES256 algorithm support
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        
        // Set up JWK source
        JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL("https://solidcommunity.net/.oidc/jwks"));
        
        // Create key selector for ES256 algorithm
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.ES256, jwkSource);
        jwtProcessor.setJWSKeySelector(keySelector);
        
        // Allow both JWT and at+jwt type headers (for DPoP access tokens)
        jwtProcessor.setJWSTypeVerifier(
            new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("JWT"), new JOSEObjectType("at+jwt"))
        );
        
        // Create decoder
        NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
        
        // Set up standard JWT validation with issuer check
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("https://solidcommunity.net/"));
        
        // Wrap decoder to log JWT details on failure
        return token -> {
            try {
                JWT jwt = JWTParser.parse(token);
                log.info("==== JWT Debug Info ====");
                log.info("JWT Header: {}", jwt.getHeader().toJSONObject());
                log.info("JWT Claims: {}", jwt.getJWTClaimsSet().toJSONObject());
                log.info("========================");
                return decoder.decode(token);
            } catch (Exception e) {
                try {
                    JWT jwt = JWTParser.parse(token);
                    log.error("==== JWT Validation Failed ====");
                    log.error("JWT Header: {}", jwt.getHeader().toJSONObject());
                    log.error("JWT Claims: {}", jwt.getJWTClaimsSet().toJSONObject());
                    log.error("Error: {}", e.getMessage());
                    log.error("================================");
                } catch (Exception parseError) {
                    log.error("Failed to parse JWT for debugging: {}", parseError.getMessage());
                }
                if (e instanceof JwtException) {
                    throw (JwtException) e;
                }
                throw new JwtException("JWT validation failed", e);
            }
        };
    }
}
