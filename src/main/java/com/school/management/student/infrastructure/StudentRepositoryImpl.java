package com.school.management.student.infrastructure;

import com.school.management.student.domain.Student;
import com.school.management.student.domain.StudentStatus;
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
public class StudentRepositoryImpl implements StudentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Student> search(String schoolId, StudentStatus status, String search, Pageable pageable) {
        Criteria criteria = Criteria.where("deleted").is(false)
                .and("school_id").is(schoolId);

        if (status != null) {
            criteria = criteria.and("status").is(status.name());
        }
        if (search != null && !search.isBlank()) {
            Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
            criteria = criteria.andOperator(
                    new Criteria().orOperator(
                            Criteria.where("first_name").regex(pattern),
                            Criteria.where("last_name").regex(pattern),
                            Criteria.where("admission_number").regex(pattern)));
        }

        Query query = new Query(criteria).with(pageable);
        Query countQuery = new Query(criteria);

        List<Student> students = mongoTemplate.find(query, Student.class);
        long count = mongoTemplate.count(countQuery, Student.class);

        return new PageImpl<>(students, pageable, count);
    }
}
