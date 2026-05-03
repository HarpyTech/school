INSERT INTO
    roles (id, name, description)
SELECT CAST(NEWID () AS VARCHAR(36)), 'ADMIN', 'Platform Super Administrator'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'ADMIN'
    );

INSERT INTO
    roles (id, name, description)
SELECT CAST(NEWID () AS VARCHAR(36)), 'SCHOOL_ADMIN', 'School Administrator'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'SCHOOL_ADMIN'
    );

INSERT INTO
    roles (id, name, description)
SELECT CAST(NEWID () AS VARCHAR(36)), 'TEACHER', 'Teacher'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'TEACHER'
    );

INSERT INTO
    roles (id, name, description)
SELECT CAST(NEWID () AS VARCHAR(36)), 'STUDENT', 'Student'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'STUDENT'
    );

INSERT INTO
    roles (id, name, description)
SELECT CAST(NEWID () AS VARCHAR(36)), 'PARENT', 'Parent/Guardian'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'PARENT'
    );