-- database schema deletion
DROP DATABASE IF EXISTS buddb;

-- user deletion
DROP USER IF EXISTS 'budapp'@'%';

-- schema creation
CREATE DATABASE IF NOT EXISTS buddb;

-- user creation
CREATE USER IF NOT EXISTS 'budapp'@'%' IDENTIFIED BY 'budpassword';
GRANT ALL ON buddb.* TO 'budapp'@'%';

-- select db
USE buddb;

-- create tables
CREATE TABLE book (
    book_id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    subtitle VARCHAR(255),
    author VARCHAR(255),
    publisher VARCHAR(255),
    edition VARCHAR(255),
    year VARCHAR(255),
    language VARCHAR(255),
    isbn_10 VARCHAR(255),
    isbn_13 VARCHAR(255),
    notes VARCHAR(255),
    retailprice DOUBLE PRECISION,
    libraryprice DOUBLE PRECISION,
    amazonlink VARCHAR(255),
    library_section VARCHAR(255),
    imagelink VARCHAR(255),
    PRIMARY KEY (book_id)
);

CREATE TABLE book_quantity (
    branch_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    total_quantity INTEGER,
    available_quantity INTEGER,
    PRIMARY KEY (branch_id , book_id)
);

CREATE TABLE book_tag (
    book_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL
);

CREATE TABLE branch (
    branch_id BIGINT NOT NULL AUTO_INCREMENT,
    branch_name VARCHAR(255),
    branch_code VARCHAR(255),
    branch_address VARCHAR(255),
    PRIMARY KEY (branch_id)
);

CREATE TABLE class_code (
    class_id INTEGER NOT NULL AUTO_INCREMENT,
    class_code VARCHAR(255),
    PRIMARY KEY (class_id)
);

CREATE TABLE faculty_class (
    loaner_id BIGINT NOT NULL,
    class_id INTEGER NOT NULL
);

CREATE TABLE librarian (
    librarian_id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255),
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(255),
    PRIMARY KEY (librarian_id)
);

CREATE TABLE loaner (
    loaner_id BIGINT NOT NULL AUTO_INCREMENT,
    school_id VARCHAR(255),
    is_student BIT,
    salutation VARCHAR(255),
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    father_name VARCHAR(255),
    mother_name VARCHAR(255),
    PRIMARY KEY (loaner_id)
);

CREATE TABLE tag (
    tag_id BIGINT NOT NULL AUTO_INCREMENT,
    tag_name VARCHAR(255),
    PRIMARY KEY (tag_id)
);

CREATE TABLE transaction (
    trn_id BIGINT NOT NULL AUTO_INCREMENT,
    trn_datetime DATETIME(6),
    trn_type INTEGER,
    branch_id BIGINT,
    coordinator_id BIGINT,
    loaner_id BIGINT,
    PRIMARY KEY (trn_id)
);

CREATE TABLE trn_quantities (
    trn_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    copies INTEGER,
    PRIMARY KEY (trn_id , book_id)
);

ALTER TABLE book_quantity
ADD CONSTRAINT fk_quantity_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id);

ALTER TABLE book_quantity
ADD CONSTRAINT fk_quantity_book FOREIGN KEY (book_id) REFERENCES book (book_id);

ALTER TABLE book_tag
ADD CONSTRAINT fk_booktag_tagid FOREIGN KEY (tag_id) REFERENCES tag (tag_id);

ALTER TABLE book_tag
ADD CONSTRAINT fk_booktag_bookid FOREIGN KEY (book_id) REFERENCES book (book_id);

ALTER TABLE faculty_class
ADD CONSTRAINT fk_class_classid FOREIGN KEY (class_id) REFERENCES class_code (class_id);

ALTER TABLE faculty_class
ADD CONSTRAINT fk_faculty_employeeid FOREIGN KEY (loaner_id) REFERENCES loaner (loaner_id);

ALTER TABLE transaction
ADD CONSTRAINT fk_trn_branch FOREIGN KEY (branch_id) REFERENCES branch (branch_id);

ALTER TABLE transaction
ADD CONSTRAINT fk_trn_coordinator FOREIGN KEY (coordinator_id) REFERENCES librarian (librarian_id);

ALTER TABLE transaction
ADD CONSTRAINT fk_trn_loaner FOREIGN KEY (loaner_id) REFERENCES loaner (loaner_id);

ALTER TABLE trn_quantities
ADD CONSTRAINT fk_trnquan_trn FOREIGN KEY (trn_id) REFERENCES transaction (trn_id);

ALTER TABLE trn_quantities
ADD CONSTRAINT fk_trnquan_book FOREIGN KEY (book_id) REFERENCES book (book_id);
