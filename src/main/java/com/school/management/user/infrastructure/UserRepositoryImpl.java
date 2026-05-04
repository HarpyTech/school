package com.school.management.user.infrastructure;

import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<User> search(String schoolId, UserStatus status, String search, Pageable pageable) {
        Criteria criteria = Criteria.where("deleted").is(false);

        if (schoolId != null && !schoolId.isBlank()) {
            criteria = criteria.and("school_id").is(schoolId);
        }
        if (status != null) {
            criteria = criteria.and("status").is(status.name());
        }
        if (search != null && !search.isBlank()) {
            Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
            criteria = criteria.andOperator(
                    new Criteria().orOperator(
                            Criteria.where("first_name").regex(pattern),
                            Criteria.where("last_name").regex(pattern),
                            Criteria.where("username").regex(pattern),
                            Criteria.where("email").regex(pattern)));
        }

        Query query = new Query(criteria).with(pageable);
        Query countQuery = new Query(criteria);

        List<User> users = mongoTemplate.find(query, User.class);
        long count = mongoTemplate.count(countQuery, User.class);

        return new PageImpl<>(users, pageable, count);
    }
}
