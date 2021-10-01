CREATE TABLE post (
                      id SERIAL PRIMARY KEY,
                      name TEXT,
                      created DATE
);
CREATE TABLE candidates (
                      id SERIAL PRIMARY KEY,
                      name TEXT,
                      registered DATE,
                      city_id  INTEGER REFERENCES city(id)
);
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name TEXT,
                       email TEXT UNIQUE,
                       password TEXT
);

CREATE TABLE city
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);
INSERT INTO city(name) VALUES ('Москва');
INSERT INTO city(name) VALUES ('Санкт-Петербург');
INSERT INTO city(name) VALUES ('Екатеринбург');
INSERT INTO city(name) VALUES ('Сочи');
INSERT INTO city(name) VALUES ('Новосибирск');
INSERT INTO city(name) VALUES ('Саратов');