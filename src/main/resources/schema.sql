DROP TABLE IF EXISTS CONTACT;

CREATE TABLE CONTACT(
    id int AUTO_INCREMENT,
    email VARCHAR (20),
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    valid_email BOOLEAN NULL,
    primary key (id)
);
