package dev.oauth.resourceserver.profile.dto;

import dev.oauth.resourceserver.profile.entity.ResourceMemberProfile;

import java.time.LocalDate;

public record MyPageResponse(
        String userId,
        String name,
        String email,
        String gender,
        LocalDate birthdate
) {
    public static MyPageResponse from(ResourceMemberProfile profile) {
        return from(profile, true, true, true, true);
    }

    public static MyPageResponse from(ResourceMemberProfile profile, boolean includeName, boolean includeEmail,
                                      boolean includeGender, boolean includeBirthdate) {
        return new MyPageResponse(
                profile.getUserId(),
                includeName ? profile.getName() : null,
                includeEmail ? profile.getEmail() : null,
                includeGender && profile.getGender() != null ? profile.getGender().name() : null,
                includeBirthdate ? profile.getBirthdate() : null
        );
    }
}
