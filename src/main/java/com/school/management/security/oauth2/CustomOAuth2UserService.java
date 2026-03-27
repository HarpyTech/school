package com.school.management.security.oauth2;

import com.school.management.common.exception.BusinessException;
import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import com.school.management.user.infrastructure.RoleRepository;
import com.school.management.user.infrastructure.UserRepository;
import com.school.management.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Handles OAuth2 user registration and login via Google.
 * If the user doesn't exist, they are auto-registered with STUDENT role.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = extractUserInfo(registrationId, oAuth2User.getAttributes());

        if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        User user = userRepository.findByEmail(userInfo.getEmail())
                .map(existing -> updateOAuth2User(existing, userInfo))
                .orElseGet(() -> registerOAuth2User(userInfo));

        return UserPrincipal.create(user);
    }

    private OAuth2UserInfo extractUserInfo(String registrationId, java.util.Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        throw new BusinessException("OAuth2 provider [" + registrationId + "] is not supported");
    }

    private User registerOAuth2User(OAuth2UserInfo userInfo) {
        Role studentRole = roleRepository.findByName(RoleName.STUDENT)
                .orElseThrow(() -> new BusinessException("Default role not configured"));

        String[] nameParts = userInfo.getName().split(" ", 2);

        User user = new User();
        user.setEmail(userInfo.getEmail());
        user.setUsername(generateUsername(userInfo.getEmail()));
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setProfilePicture(userInfo.getImageUrl());
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(studentRole));
        user.setPassword(""); // OAuth2 users have no local password

        return userRepository.save(user);
    }

    private User updateOAuth2User(User existing, OAuth2UserInfo userInfo) {
        existing.setProfilePicture(userInfo.getImageUrl());
        existing.setEmailVerified(true);
        return userRepository.save(existing);
    }

    private String generateUsername(String email) {
        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
        String candidate = base;
        int counter = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + counter++;
        }
        return candidate;
    }
}
