package dev.oauth.resourceserver.controller;

import dev.oauth.resourceserver.profile.dto.MyPageResponse;
import dev.oauth.resourceserver.profile.service.ProfileService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

    private final ProfileService profileService;

    public UserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping(value = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> publicEndpoint() {
        return Map.of(
                "message", "public ok",
                "authenticated", false
        );
    }

    @GetMapping(value = "/api/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "authenticated user",
                "sub", jwt.getSubject(),
                "iss", jwt.getIssuer() != null ? jwt.getIssuer().toString() : null,
                "scopes", extractScopes(jwt)
        );
    }

    @GetMapping(value = "/api/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> profile(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "profile scope granted",
                "sub", jwt.getSubject(),
                "name", getStringClaim(jwt, "name"),
                "preferred_username", getStringClaim(jwt, "preferred_username"),
                "scopes", extractScopes(jwt)
        );
    }

    @GetMapping(value = "/api/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> email(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "email scope granted",
                "sub", jwt.getSubject(),
                "email", getStringClaim(jwt, "email"),
                "email_verified", jwt.getClaim("email_verified"),
                "scopes", extractScopes(jwt)
        );
    }

    @GetMapping(value = "/api/mypage", produces = MediaType.APPLICATION_JSON_VALUE)
    public MyPageResponse myPage(@AuthenticationPrincipal Jwt jwt) {
        String userId = resolveUserId(jwt);
        Set<String> scopes = extractScopes(jwt);

        try {
            return profileService.getMyPage(
                    userId,
                    scopes.contains("name"),
                    scopes.contains("email"),
                    scopes.contains("gender"),
                    scopes.contains("birthdate")
            );
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    private Set<String> extractScopes(Jwt jwt) {
        Set<String> scopes = new HashSet<>();
        addScopes(scopes, jwt.getClaims().get("scope"));
        addScopes(scopes, jwt.getClaims().get("scp"));
        return Set.copyOf(scopes);
    }

    private String getStringClaim(Jwt jwt, String claimName) {
        Object value = jwt.getClaims().get(claimName);
        return value != null ? value.toString() : null;
    }

    private String resolveUserId(Jwt jwt) {
        String preferredUsername = getStringClaim(jwt, "preferred_username");
        String userId = getStringClaim(jwt, "user_id");

        if (preferredUsername != null && !preferredUsername.isBlank()) {
            return preferredUsername;
        }
        if (userId != null && !userId.isBlank()) {
            return userId;
        }
        return jwt.getSubject();
    }

    private void addScopes(Set<String> scopes, Object scopeClaim) {
        if (scopeClaim instanceof String scopeString) {
            for (String scope : scopeString.split(" ")) {
                if (!scope.isBlank()) {
                    scopes.add(scope);
                }
            }
            return;
        }
        if (scopeClaim instanceof Collection<?> collection) {
            for (Object value : collection) {
                if (value != null) {
                    scopes.add(value.toString());
                }
            }
        }
    }
}
