package com.school.management.user.infrastructure;

import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> search(String schoolId, UserStatus status, String search, Pageable pageable);
}
