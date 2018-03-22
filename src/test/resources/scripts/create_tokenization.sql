DROP TABLE IF EXISTS format_scheme;
DROP TABLE IF EXISTS client_database;
DROP TABLE IF EXISTS database_owner;

CREATE TABLE format_scheme (
  id INT NOT NULL,
  name VARCHAR(8) NOT NULL,
  description VARCHAR(4000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX name_idx ON format_scheme(name);


CREATE TABLE database_owner (
  database VARCHAR(16) NOT NULL,
  description VARCHAR(4000) NOT NULL,
  owner_id VARCHAR(64) NOT NULL,
  owner_email VARCHAR(64) NOT NULL,
  PRIMARY KEY (database)
);

CREATE TABLE client_database (
  client_id VARCHAR(36) NOT NULL,
  database VARCHAR(16) NOT NULL,
  PRIMARY KEY (client_id),
  FOREIGN KEY (database) REFERENCES database_owner(database)
);

INSERT INTO database_owner(database, description, owner_id, owner_email)
VALUES('vault000', 'The default vault database', 'admin', 'admin@networknt.com');

INSERT INTO client_database (client_id, database)
VALUES('f7d42348-c647-4efb-a52d-4c5787421e72', 'vault000');

INSERT INTO format_scheme (id, name, description)
VALUES (1, 'GUID', 'Globally Unique Identifier. This format requires no validation and will return a GUID as the token.');
INSERT INTO format_scheme (id, name, description)
VALUES (2, 'LN', 'LUHN Compliant Numeric. This format is used to tokenize a social insurance number or social security number.');
INSERT INTO format_scheme (id, name, description)
VALUES (3, 'N', 'Random Numeric. This format is used to tokenize a account number or any number without validation.');
INSERT INTO format_scheme (id, name, description)
VALUES (4, 'LN4', 'LUHN Compliant Numeric token retaining the original last 4 digits of the number. Can be used as credit card number.');
INSERT INTO format_scheme (id, name, description)
VALUES (5, 'AN', 'Alpha Numeric, length preserving token.');
INSERT INTO format_scheme (id, name, description)
VALUES (6, 'AN4', 'Alpha Numeric, length preserving token retaining the original last 4 characters.');

