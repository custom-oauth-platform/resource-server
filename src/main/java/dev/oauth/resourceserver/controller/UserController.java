package dev.oauth.resourceserver.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        return Map.of(
                "message", "public ok",
                "authenticated", false
        );
    }

    @GetMapping("/api/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "authenticated user",
                "sub", jwt.getSubject(),
                "iss", jwt.getIssuer() != null ? jwt.getIssuer().toString() : null,
                "scopes", extractScopes(jwt)
        );
    }

    @GetMapping("/api/profile")
    public Map<String, Object> profile(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "profile scope granted",
                "sub", jwt.getSubject(),
                "name", getStringClaim(jwt, "name"),
                "preferred_username", getStringClaim(jwt, "preferred_username"),
                "scopes", extractScopes(jwt)
        );
    }

    @GetMapping("/api/email")
    public Map<String, Object> email(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "email scope granted",
                "sub", jwt.getSubject(),
                "email", getStringClaim(jwt, "email"),
                "email_verified", jwt.getClaim("email_verified"),
                "scopes", extractScopes(jwt)
        );
    }

    private Object extractScopes(Jwt jwt) {
        Object scope = jwt.getClaims().get("scope");
        if (scope instanceof String scopeString) {
            return List.of(scopeString.split(" "));
        }
        return scope;
    }

    private String getStringClaim(Jwt jwt, String claimName) {
        Object value = jwt.getClaims().get(claimName);
        return value != null ? value.toString() : null;
    }
}