package dev.oauth.resourceserver.profile.service;

import dev.oauth.resourceserver.profile.dto.MyPageResponse;
import dev.oauth.resourceserver.profile.entity.ResourceMemberProfile;
import dev.oauth.resourceserver.profile.repository.ResourceMemberProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ResourceMemberProfileRepository profileRepository;

    public ProfileService(ResourceMemberProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public MyPageResponse getMyPage(String userId) {
        return getMyPage(userId, true, true, true, true);
    }

    public MyPageResponse getMyPage(String userId, boolean includeName, boolean includeEmail,
                                    boolean includeGender, boolean includeBirthdate) {
        ResourceMemberProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다. userId=" + userId));

        return MyPageResponse.from(profile, includeName, includeEmail, includeGender, includeBirthdate);
    }
}
