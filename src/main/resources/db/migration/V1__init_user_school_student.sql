IF OBJECT_ID ('dbo.roles', 'U') IS NULL BEGIN
CREATE TABLE roles (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)
) END;

IF OBJECT_ID ('dbo.schools', 'U') IS NULL BEGIN
CREATE TABLE schools (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(25),
    address_street VARCHAR(255),
    address_city VARCHAR(100),
    address_state VARCHAR(100),
    address_country VARCHAR(100),
    address_zip_code VARCHAR(20),
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 (6) NOT NULL,
    updated_at DATETIME2 (6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    deleted BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_schools_code UNIQUE (code)
) END;

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_school_name'
        AND object_id = OBJECT_ID ('dbo.schools')
)
CREATE INDEX idx_school_name ON schools (name);

IF OBJECT_ID ('dbo.users', 'U') IS NULL BEGIN
CREATE TABLE users (
    id VARCHAR(36) NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    profile_picture VARCHAR(500),
    school_id VARCHAR(36),
    status VARCHAR(30) NOT NULL,
    email_verified BIT NOT NULL DEFAULT 0,
    email_verification_token VARCHAR(100),
    email_verification_token_expiry DATETIME2 (6),
    password_reset_token VARCHAR(100),
    password_reset_token_expiry DATETIME2 (6),
    last_login_at DATETIME2 (6),
    created_at DATETIME2 (6) NOT NULL,
    updated_at DATETIME2 (6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    deleted BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_school FOREIGN KEY (school_id) REFERENCES schools (id)
) END;

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_user_school'
        AND object_id = OBJECT_ID ('dbo.users')
)
CREATE INDEX idx_user_school ON users (school_id);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_user_status'
        AND object_id = OBJECT_ID ('dbo.users')
)
CREATE INDEX idx_user_status ON users (status);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_user_password_reset_token'
        AND object_id = OBJECT_ID ('dbo.users')
)
CREATE INDEX idx_user_password_reset_token ON users (password_reset_token);

IF OBJECT_ID ('dbo.user_roles', 'U') IS NULL BEGIN
CREATE TABLE user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
) END;

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_user_roles_user_id'
        AND object_id = OBJECT_ID ('dbo.user_roles')
)
CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_user_roles_role_id'
        AND object_id = OBJECT_ID ('dbo.user_roles')
)
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

IF OBJECT_ID ('dbo.refresh_tokens', 'U') IS NULL BEGIN
CREATE TABLE refresh_tokens (
    id VARCHAR(36) NOT NULL,
    token VARCHAR(255) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    expires_at DATETIME2 (6) NOT NULL,
    revoked BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 (6) NOT NULL,
    updated_at DATETIME2 (6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    deleted BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_refresh_token_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
) END;

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_refresh_token_user'
        AND object_id = OBJECT_ID ('dbo.refresh_tokens')
)
CREATE INDEX idx_refresh_token_user ON refresh_tokens (user_id);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_refresh_token_expiry'
        AND object_id = OBJECT_ID ('dbo.refresh_tokens')
)
CREATE INDEX idx_refresh_token_expiry ON refresh_tokens (expires_at);

IF OBJECT_ID ('dbo.school_branches', 'U') IS NULL BEGIN
CREATE TABLE school_branches (
    id VARCHAR(36) NOT NULL,
    school_id VARCHAR(36) NOT NULL,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(30) NOT NULL,
    principal_name VARCHAR(150),
    address_street VARCHAR(255),
    address_city VARCHAR(100),
    address_state VARCHAR(100),
    address_country VARCHAR(100),
    address_zip_code VARCHAR(20),
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 (6) NOT NULL,
    updated_at DATETIME2 (6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    deleted BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_branch_school FOREIGN KEY (school_id) REFERENCES schools (id)
) END;

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_branch_school_id'
        AND object_id = OBJECT_ID ('dbo.school_branches')
)
CREATE INDEX idx_branch_school_id ON school_branches (school_id);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_branch_code'
        AND object_id = OBJECT_ID ('dbo.school_branches')
)
CREATE INDEX idx_branch_code ON school_branches (code);

IF OBJECT_ID ('dbo.academic_years', 'U') IS NULL BEGIN
CREATE TABLE academic_years (
    id VARCHAR(36) NOT NULL,
    school_id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    current_year BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 (6) NOT NULL,
    updated_at DATETIME2 (6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    deleted BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_ay_school FOREIGN KEY (school_id) REFERENCES schools (id)
) END;

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_ay_school_id'
        AND object_id = OBJECT_ID ('dbo.academic_years')
)
CREATE INDEX idx_ay_school_id ON academic_years (school_id);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_ay_name'
        AND object_id = OBJECT_ID ('dbo.academic_years')
)
CREATE INDEX idx_ay_name ON academic_years (name);

IF OBJECT_ID ('dbo.parent_guardians', 'U') IS NULL BEGIN
CREATE TABLE parent_guardians (
    id VARCHAR(36) NOT NULL,
    school_id VARCHAR(36) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(25),
    relationship VARCHAR(50),
    address_street VARCHAR(255),
    address_city VARCHAR(100),
    address_state VARCHAR(100),
    address_country VARCHAR(100),
    address_zip_code VARCHAR(20),
    created_at DATETIME2 (6) NOT NULL,
    updated_at DATETIME2 (6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    deleted BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_parent_school FOREIGN KEY (school_id) REFERENCES schools (id)
) END;

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_parent_school'
        AND object_id = OBJECT_ID ('dbo.parent_guardians')
)
CREATE INDEX idx_parent_school ON parent_guardians (school_id);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_parent_email'
        AND object_id = OBJECT_ID ('dbo.parent_guardians')
)
CREATE INDEX idx_parent_email ON parent_guardians (email);

IF OBJECT_ID ('dbo.students', 'U') IS NULL BEGIN
CREATE TABLE students (
    id VARCHAR(36) NOT NULL,
    school_id VARCHAR(36) NOT NULL,
    admission_number VARCHAR(40) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    date_of_birth DATE,
    admission_date DATE NOT NULL,
    current_grade VARCHAR(30),
    section VARCHAR(30),
    status VARCHAR(30) NOT NULL,
    parent_id VARCHAR(36),
    address_street VARCHAR(255),
    address_city VARCHAR(100),
    address_state VARCHAR(100),
    address_country VARCHAR(100),
    address_zip_code VARCHAR(20),
    documents_json VARCHAR(MAX),
    created_at DATETIME2 (6) NOT NULL,
    updated_at DATETIME2 (6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    deleted BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_students_admission_number UNIQUE (admission_number),
    CONSTRAINT fk_student_school FOREIGN KEY (school_id) REFERENCES schools (id),
    CONSTRAINT fk_student_parent FOREIGN KEY (parent_id) REFERENCES parent_guardians (id)
) END;

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_student_school'
        AND object_id = OBJECT_ID ('dbo.students')
)
CREATE INDEX idx_student_school ON students (school_id);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_student_status'
        AND object_id = OBJECT_ID ('dbo.students')
)
CREATE INDEX idx_student_status ON students (status);

IF NOT EXISTS (
    SELECT *
    FROM sys.indexes
    WHERE
        name = 'idx_student_grade'
        AND object_id = OBJECT_ID ('dbo.students')
)
CREATE INDEX idx_student_grade ON students (current_grade);