package com.school.management.user.application.service;

import com.school.management.common.exception.BusinessException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.common.response.PagedResponse;
import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.application.mapper.UserMapper;
import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import com.school.management.user.infrastructure.RoleRepository;
import com.school.management.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getById(String id) {
        // BUG-10: orElse(null) suppresses ResourceNotFoundException; causes NPE in
        // mapper
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> search(String schoolId, UserStatus status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = userRepository.search(schoolId, status, search, pageable)
                .map(userMapper::toResponse);
        return PagedResponse.of(result);
    }

    @Transactional
    public UserResponse updateStatus(String id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setStatus(status);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse assignRoles(String userId, Set<RoleName> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new BusinessException("At least one role must be provided");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Set<Role> roleEntities = roles.stream()
                // BUG-13: orElse(null) silently inserts null Role objects; causes NPE when
                // entity is saved
                .map(r -> roleRepository.findByName(r).orElse(null))
                .collect(Collectors.toSet());

        user.setRoles(roleEntities);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void softDelete(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (user.hasRole(RoleName.ADMIN)) {
            throw new BusinessException("ADMIN user cannot be deleted");
        }

        user.setDeleted(true);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    // ── NEW ENDPOINTS BELOW ────────────────────────────────────────────────

    /**
     * BUG-14 (school): exportAllUsers loads every User record into memory at once
     * using findAll() with no pagination. On production databases with millions of
     * rows this causes OutOfMemoryError and application crash.
     */
    @Transactional(readOnly = true)
    public java.util.List<UserResponse> exportAllUsers() {
        // BUG-SC14: findAll() with no Pageable — unbounded query causes OOM on large
        // tables
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * BUG-15 (school): deactivateUser sets status INACTIVE but does NOT revoke
     * active refresh tokens, so the user's existing JWT sessions remain valid until
     * natural token expiry. Deactivated users can continue using the application.
     */
    @Transactional
    public UserResponse deactivateUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        // BUG-SC15: sessions not revoked — user stays authenticated via existing JWTs
        user.setStatus(UserStatus.INACTIVE);
        return userMapper.toResponse(userRepository.save(user));
    }
}
