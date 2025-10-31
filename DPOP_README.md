# DPoP Implementation Guide

## Overview

This Spring Security application has been configured as an OAuth 2.0 Resource Server with DPoP (Demonstrating Proof of Possession) support. DPoP is a mechanism that binds access tokens to a specific client's public/private key pair, preventing token theft and replay attacks.

## What Was Implemented

### 1. **SecurityConfig.java**
- Enabled OAuth 2.0 Resource Server with JWT authentication
- Configured DPoP support using `.dpop(dpop -> {})`
- Changed authorization to require authentication for all endpoints

### 2. **DPoPContextFilter.java** (NEW)
A servlet filter that intercepts all requests and prints:
- HTTP method and URI
- Authorization header (checks for `DPoP` vs `Bearer` scheme)
- DPoP header (the DPoP Proof JWT)
- Parses and displays the DPoP Proof including:
  - Header (typ, alg, jwk public key)
  - Claims (htm, htu, ath, jti, iat)
  - Explanations of each claim

### 3. **DemoController.java**
Enhanced to extract and print JWT access token information:
- Subject, issuer, issued/expires timestamps
- **CNF (confirmation) claim** containing the JWK thumbprint (jkt)
- All JWT claims and headers
- Authentication details

### 4. **application.yaml**
- Added OAuth 2.0 resource server configuration
- Configured JWT issuer URI (defaults to `http://localhost:9000`)

## How DPoP Works

1. **Client** generates a public/private key pair
2. **Client** requests an access token from the **Authorization Server**:
   - Sends a DPoP proof JWT in the `DPoP` header
   - DPoP proof contains the public key in the `jwk` claim
3. **Authorization Server** binds the access token to the public key:
   - Embeds the public key thumbprint in the access token's `cnf.jkt` claim
   - Returns a token with `token_type: "DPoP"`
4. **Client** makes a request to the **Resource Server** (this app):
   - Sends access token in `Authorization: DPoP <token>` header
   - Sends a NEW DPoP proof in the `DPoP` header with:
     - `htm`: HTTP method (e.g., "GET")
     - `htu`: Target URI
     - `ath`: Hash of the access token
     - Same public key as used in step 2
5. **Resource Server** validates:
   - The DPoP proof signature using the public key in the proof
   - The public key in the proof matches the thumbprint in the access token (`cnf.jkt`)
   - The `ath` claim matches the hash of the received access token
   - The `htm` and `htu` match the actual request

## Configuration

### Required Configuration

Set the authorization server's issuer URI in `application.yaml` or as an environment variable:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:9000
```

Or use environment variable:
```bash
export OAUTH2_ISSUER_URI=https://your-auth-server.com
```

Alternatively, use `jwk-set-uri` directly if you know the JWKS endpoint:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:9000/.well-known/jwks.json
```

## Testing

### Option 1: Using a DPoP-enabled Authorization Server

1. Set up an authorization server that supports DPoP (e.g., Spring Authorization Server with DPoP support)
2. Configure the `issuer-uri` in `application.yaml`
3. Use a DPoP-enabled client to obtain a DPoP-bound access token
4. Make a request with both the access token and DPoP proof

### Option 2: Manual Testing with curl (for demonstration)

**Note:** This requires generating a proper DPoP proof JWT with a matching key pair.

Example request structure:
```bash
curl -X GET http://localhost:8080/ \
  -H "Authorization: DPoP eyJhbGc..." \
  -H "DPoP: eyJraWQiOiJyc2E..."
```

### Option 3: Create a Test Client

You can create a simple Java client that:
1. Generates an RSA key pair
2. Creates a DPoP proof JWT
3. Exchanges it for a DPoP-bound access token
4. Makes requests to this resource server

See the Spring Security documentation for code examples:
https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/dpop-tokens.html

## What Gets Printed

When a request is received, you'll see console output like:

```
======================================
=== DPoP Request Context ===
======================================

1. HTTP Request Details:
   Method: GET
   URI: /
   URL: http://localhost:8080/

2. Authorization Header:
   Full header: DPoP eyJhbGc...
   Scheme: DPoP (DPoP-bound token)
   Access Token (first 20 chars): eyJhbGc...

3. DPoP Header (DPoP Proof JWT):
   DPoP header present: Yes
   DPoP Proof JWT (first 50 chars): eyJraWQiOiJyc2E...

   DPoP Proof Header:
      typ: dpop+jwt
      alg: RS256
      jwk: (JWK public key present)
         kty: RSA
         kid: rsa-jwk-kid
         n: 3FlqJr5TRskIQIgdE3Dd7D9...

   DPoP Proof Claims:
      htm: GET
      htu: https://resource.example.com/resource
      ath: fUHyO2r2Z3DZ53EsNrWBb0xWXoaNy59IiKCAqksmQEo
      iat: 1746807138
      jti: 3c2ee9bb-03ac-40cf-b812-00bfba341cee

   DPoP Proof Claim Explanations:
      htm (HTTP Method): Binds proof to HTTP method
      htu (HTTP URI): Binds proof to target URI
      ath (Access Token Hash): SHA-256 hash of access token
      jti (JWT ID): Unique identifier to prevent replay attacks
      iat (Issued At): Timestamp when proof was created

======================================

=== DPoP Context Information ===

1. JWT Token Information:
   Subject: [email protected]
   Issuer: https://server.example.com
   Issued At: 2019-07-04T...
   Expires At: 2019-07-04T...

2. DPoP Confirmation Claim (cnf):
   CNF Claim found: {jkt=CQMknzRoZ5YUi7vS58jck1q8TmZT8wiIiXrCN1Ny4VU}
   JWK Thumbprint (jkt): CQMknzRoZ5YUi7vS58jck1q8TmZT8wiIiXrCN1Ny4VU
   -> This is the SHA-256 thumbprint of the DPoP public key

3. All JWT Claims:
   sub: [email protected]
   iss: https://server.example.com
   cnf: {jkt=CQMknzRoZ5YUi7vS58jck1q8TmZT8wiIiXrCN1Ny4VU}
   ...

=== End DPoP Context Information ===
```

## Key Components to Understand

### cnf.jkt (Confirmation Claim)
- The `cnf` (confirmation) claim in the access token contains the `jkt` (JWK thumbprint)
- This is the SHA-256 hash of the DPoP public key
- Spring Security automatically validates that the key in the DPoP proof matches this thumbprint

### DPoP Proof
- A JWT that proves possession of the private key
- Must be generated fresh for each request
- Contains the `ath` (access token hash) to bind the proof to the specific access token

### Authorization Header Scheme
- DPoP-bound tokens use `Authorization: DPoP <token>` instead of `Authorization: Bearer <token>`
- This signals to the resource server that DPoP validation is required

## Running the Application

```bash
./mvnw spring-boot:run
```

Or with a specific issuer URI:
```bash
OAUTH2_ISSUER_URI=https://your-auth-server.com ./mvnw spring-boot:run
```

## Next Steps

1. **Set up an Authorization Server** that supports DPoP
2. **Configure the issuer URI** in `application.yaml`
3. **Create a client** that can generate DPoP proofs and access tokens
4. **Test the endpoint** at `http://localhost:8080/`
5. **Observe the console output** showing the complete DPoP context

## References

- [Spring Security DPoP Documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/dpop-tokens.html)
- [RFC 9449: OAuth 2.0 Demonstrating Proof of Possession](https://datatracker.ietf.org/doc/html/rfc9449)
- [Spring Authorization Server](https://spring.io/projects/spring-authorization-server)
