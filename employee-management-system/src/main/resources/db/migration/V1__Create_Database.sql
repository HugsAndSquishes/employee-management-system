
/*
This file creates the database if it does not already exist.
*/

CREATE DATABASE IF NOT EXISTS employee_database; -- TODO: database name should come from config.properties
USE employee_database;
USE employee_database;

CREATE TABLE IF NOT EXISTS employees (
    empID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    employeeName VARCHAR(255),
    SSN VARCHAR(9) NOT NULL UNIQUE, -- No two rows can have the same SSN calue
    jobTitle VARCHAR(255),
    division VARCHAR(255),
    salary DECIMAL(10,2),
    payInfo VARCHAR(255)
);
