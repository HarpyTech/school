INSERT INTO roles (id, name, description)
SELECT UUID(), 'ADMIN', 'Platform Super Administrator'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (id, name, description)
SELECT UUID(), 'SCHOOL_ADMIN', 'School Administrator'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'SCHOOL_ADMIN');

INSERT INTO roles (id, name, description)
SELECT UUID(), 'TEACHER', 'Teacher'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'TEACHER');

INSERT INTO roles (id, name, description)
SELECT UUID(), 'STUDENT', 'Student'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'STUDENT');

INSERT INTO roles (id, name, description)
SELECT UUID(), 'PARENT', 'Parent/Guardian'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'PARENT');
