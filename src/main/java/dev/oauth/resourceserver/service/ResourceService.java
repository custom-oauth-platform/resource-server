package dev.oauth.resourceserver.service;

import dev.oauth.resourceserver.entity.MemberProfile;
import dev.oauth.resourceserver.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor // 필드 주입을 위해 필수
public class ResourceService {

    // 1. 레포지토리를 상단에 필드로 선언 (final 필수)
    private final MemberProfileRepository memberProfileRepository;

    public Map<String, Object> getUserInfo(Jwt jwt) {
        String userId = jwt.getSubject();

        // 2. 주입받은 필드를 사용하여 조회
        MemberProfile profile = memberProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("프로필 정보를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();
        result.put("sub", profile.getUserId());
        result.put("name", profile.getName());
        result.put("gender", profile.getGender());
        result.put("birthdate", profile.getBirthdate());
        return result;
    }

    public MemberProfile getMemberProfile(String userId) {
        return memberProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + userId));
    }
}