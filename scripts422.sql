CREATE TABLE persons (
    id SERIAL UNIQUE PRIMARY KEY,
    name TEXT,
    age INTEGER,
    license BOOLEAN,
    cars_id SERIAL REFERENCES cars(id));

CREATE TABLE cars (
    id SERIAL UNIQUE,
    brand TEXT,
    model TEXT,
    price NUMERIC);
