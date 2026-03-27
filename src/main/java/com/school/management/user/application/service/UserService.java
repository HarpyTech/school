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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
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
                .map(r -> roleRepository.findByName(r)
                        .orElseThrow(() -> new BusinessException("Role not found: " + r)))
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
}
