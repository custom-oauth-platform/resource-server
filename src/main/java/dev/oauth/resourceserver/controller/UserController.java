package dev.oauth.resourceserver.controller;

import dev.oauth.resourceserver.profile.dto.MyPageResponse;
import dev.oauth.resourceserver.profile.service.ProfileService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/api/mypage")
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
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        }
    }

    private Set<String> extractScopes(Jwt jwt) {
        Set<String> scopes = new HashSet<>();
        addScopes(scopes, jwt.getClaims().get("scope"));
        addScopes(scopes, jwt.getClaims().get("scp"));
        return Set.copyOf(scopes);
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

    private String getStringClaim(Jwt jwt, String claimName) {
        Object value = jwt.getClaims().get(claimName);
        return value != null ? value.toString() : null;
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