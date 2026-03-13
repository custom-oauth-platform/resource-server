package dev.oauth.resourceserver.repository;

import dev.oauth.resourceserver.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
    // 로그인 아이디로 사용자를 찾는 메서드 추가
    Optional<MemberProfile> findByUserId(String userId);
}