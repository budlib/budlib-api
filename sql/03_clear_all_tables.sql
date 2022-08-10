-- select db
USE buddb;

-- delete all rows from tables
DELETE FROM trn_quantities; 
DELETE FROM transaction; 
DELETE FROM loan; 
DELETE FROM loaner; 

DELETE FROM book_tag; 
DELETE FROM book; 
DELETE FROM tag; 
