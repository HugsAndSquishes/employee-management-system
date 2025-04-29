-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS ${database_name}; -- TODO: Replace ${database_name} using config.properties
USE ${database_name};

-- Create employees table
CREATE TABLE IF NOT EXISTS employees (
    empID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    employeeName VARCHAR(255),
    --SSN VARCHAR(9) NOT NULL UNIQUE,
    jobTitle VARCHAR(255),
    division VARCHAR(255),
    salary DECIMAL(10,2),
    payInfo VARCHAR(8) NOT NULL,
    
    -- Constraints
    --CONSTRAINT chk_ssn_length CHECK (CHAR_LENGTH(SSN) = 9),
    CONSTRAINT chk_payinfo CHECK (UPPER(payInfo) IN ('FULLTIME', 'PARTTIME'))
);

-- Create trigger to auto-uppercase payInfo on INSERT
DELIMITER //

CREATE TRIGGER trg_before_insert_employees
BEFORE INSERT ON employees
FOR EACH ROW
BEGIN
    SET NEW.payInfo = UPPER(NEW.payInfo);
END;
//

DELIMITER ;

-- Create trigger to auto-uppercase payInfo on UPDATE
DELIMITER //

CREATE TRIGGER trg_before_update_employees
BEFORE UPDATE ON employees
FOR EACH ROW
BEGIN
    SET NEW.payInfo = UPPER(NEW.payInfo);
END;
//

DELIMITER ;
