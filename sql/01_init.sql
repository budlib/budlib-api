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
    library_section VARCHAR(255),
    total_quantity INTEGER,
    available_quantity INTEGER,
    notes VARCHAR(255),
    image_link VARCHAR(255),
    retail_price DOUBLE PRECISION,
    library_price DOUBLE PRECISION,
    PRIMARY KEY (book_id)
);

CREATE TABLE tag (
    tag_id BIGINT NOT NULL AUTO_INCREMENT,
    tag_name VARCHAR(255),
    PRIMARY KEY (tag_id)
);

CREATE TABLE book_tag (
    book_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    CONSTRAINT fk_booktag_book FOREIGN KEY (book_id) REFERENCES book (book_id),
    CONSTRAINT fk_booktag_tag FOREIGN KEY (tag_id) REFERENCES tag (tag_id)
);

CREATE TABLE librarian (
    librarian_id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255),
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    role INTEGER,
    PRIMARY KEY (librarian_id)
);

CREATE TABLE loaner (
    loaner_id BIGINT NOT NULL AUTO_INCREMENT,
    school_id VARCHAR(255),
    email VARCHAR(255),
    is_student BIT,
    salutation VARCHAR(255),
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    father_name VARCHAR(255),
    mother_name VARCHAR(255),
    PRIMARY KEY (loaner_id)
);

CREATE TABLE loan (
    loan_id BIGINT NOT NULL AUTO_INCREMENT,
    book_id BIGINT,
    copies INTEGER,
    loaner_id BIGINT,
    borrow_date DATE,
    due_date DATE,
    PRIMARY KEY (loan_id),
    CONSTRAINT fk_loan_book FOREIGN KEY (book_id) REFERENCES book (book_id),
    CONSTRAINT fk_loan_loaner FOREIGN KEY (loaner_id) REFERENCES loaner (loaner_id)
);

CREATE TABLE transaction (
    trn_id BIGINT NOT NULL AUTO_INCREMENT,
    trn_datetime DATETIME(6),
    trn_type INTEGER,
    librarian_id BIGINT,
    loaner_id BIGINT,
    PRIMARY KEY (trn_id),
    CONSTRAINT fk_trn_librarian FOREIGN KEY (librarian_id) REFERENCES librarian (librarian_id),
    CONSTRAINT fk_trn_loaner FOREIGN KEY (loaner_id) REFERENCES loaner (loaner_id)
);

CREATE TABLE trn_quantities (
    trn_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    copies INTEGER,
    PRIMARY KEY (trn_id , book_id),
    CONSTRAINT fk_trnqty_trn FOREIGN KEY (trn_id) REFERENCES transaction (trn_id),
    CONSTRAINT fk_trnqty_book FOREIGN KEY (book_id) REFERENCES book (book_id)
);
