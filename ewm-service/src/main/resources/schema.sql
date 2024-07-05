DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  email VARCHAR(254) NOT NULL UNIQUE,
  name VARCHAR(250) NOT NULL,
  PRIMARY KEY (id)
);