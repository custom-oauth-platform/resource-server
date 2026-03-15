package dev.oauth.resourceserver.profile.repository;

import dev.oauth.resourceserver.profile.entity.ResourceMemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceMemberProfileRepository extends JpaRepository<ResourceMemberProfile, Long> {

    Optional<ResourceMemberProfile> findByUserId(String userId);
}
