package org.mekluppie.restapp.config;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class DPoPDebugFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(DPoPDebugFilter.class);
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest httpRequest) {
            String dpopHeader = httpRequest.getHeader("DPoP");
            String authHeader = httpRequest.getHeader("Authorization");
            
            if (dpopHeader != null && authHeader != null) {
                try {
                    log.info("======== DPoP Debug ========");
                    log.info("Request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURL());
                    
                    // Parse DPoP proof
                    JWT dpopProof = JWTParser.parse(dpopHeader);
                    log.info("DPoP Proof Header: {}", dpopProof.getHeader().toJSONObject());
                    log.info("DPoP Proof Claims: {}", dpopProof.getJWTClaimsSet().toJSONObject());
                    
                    // Extract access token
                    String accessToken = authHeader.replace("DPoP ", "").trim();
                    
                    // Calculate access token hash (ath)
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(accessToken.getBytes());
                    String calculatedAth = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
                    
                    String dpopAth = dpopProof.getJWTClaimsSet().getStringClaim("ath");
                    
                    log.info("Access Token Hash (ath):");
                    log.info("  - From DPoP proof: {}", dpopAth);
                    log.info("  - Calculated: {}", calculatedAth);
                    log.info("  - Match: {}", calculatedAth.equals(dpopAth));
                    
                    // Parse access token to get cnf.jkt
                    JWT accessJwt = JWTParser.parse(accessToken);
                    Object cnf = accessJwt.getJWTClaimsSet().getClaim("cnf");
                    log.info("Access Token cnf claim: {}", cnf);
                    
                    log.info("============================");
                    
                } catch (Exception e) {
                    log.error("Error parsing DPoP proof: {}", e.getMessage(), e);
                }
            }
        }
        
        chain.doFilter(request, response);
    }
}
