package dev.oauth.resourceserver.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceService {

    public Map<String,Object> getUserInfo(Jwt jwt){

        String userId = jwt.getSubject();

        // 실제 DB 대신 Mock 데이터
        Map<String,Object> userData = Map.of(
                "name","홍길동",
                "email","hong@test.com",
                "birth","1999-01-01",
                "gender","male"
        );

        Map<String,Object> result = new HashMap<>();

        List<String> scopes = extractScopes(jwt);

        if(scopes.contains("profile")){
            result.put("name",userData.get("name"));
            result.put("birth",userData.get("birth"));
            result.put("gender",userData.get("gender"));
        }

        if(scopes.contains("email")){
            result.put("email",userData.get("email"));
        }

        result.put("sub",userId);

        return result;
    }

    private List<String> extractScopes(Jwt jwt){

        Object scope = jwt.getClaims().get("scope");

        if(scope instanceof String scopeString){
            return List.of(scopeString.split(" "));
        }

        return List.of();
    }
}