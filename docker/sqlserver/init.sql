IF NOT EXISTS (
    SELECT name
    FROM sys.databases
    WHERE
        name = 'school_management'
) BEGIN
CREATE DATABASE school_management;

END

IF NOT EXISTS (
    SELECT name
    FROM sys.databases
    WHERE
        name = 'school_test'
) BEGIN
CREATE DATABASE school_test;

END