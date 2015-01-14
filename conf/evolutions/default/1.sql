# --- First database schema
 
# --- !Ups
 
CREATE TABLE assessments (
  id                        SERIAL PRIMARY KEY,
  aid                       VARCHAR(255) NOT NULL,
  name                      VARCHAR(255) NOT NULL,
  email                     VARCHAR(255) NOT NULL
);
 
# --- !Downs
 
DROP TABLE IF EXISTS assessments;
