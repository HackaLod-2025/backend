package org.mekluppie.restapp;

//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/")
    public HelloMessage sayHello() {
        return new HelloMessage("Hello, DPoP World!");
    }

//    @GetMapping("/")
//    public HelloMessage sayHello(@AuthenticationPrincipal Jwt jwt) {
//        var jwtMap = Map.of(
//                "subject", jwt.getSubject(),
//                "issuer", jwt.getIssuer().toString(),
//                "confirmation", jwt.getClaim("cnf")
//        );
//        System.out.println("=== DPoP Context Information ===");
//        jwtMap.forEach((key, value) -> System.out.println(key + ": " + value));
//
//        return new HelloMessage("Hello, DPoP World!\nJWT Info: " + jwtMap);
//    }

    public record HelloMessage(String message) { }
}
