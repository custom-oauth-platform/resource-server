package dev.oauth.resourceserver.controller;

import dev.oauth.resourceserver.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/userinfo")
    public Map<String,Object> userinfo(@AuthenticationPrincipal Jwt jwt){

        String userId = jwt.getSubject();
        return resourceService.getUserInfo(jwt);
    }
}