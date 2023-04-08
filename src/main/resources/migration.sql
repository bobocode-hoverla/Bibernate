CREATE TABLE IF NOT EXISTS customer  (
                          id SERIAL PRIMARY KEY,
                          first_name VARCHAR(255),
                          last_name VARCHAR(255),
                          email VARCHAR(255),
                          created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS project (
                         id SERIAL PRIMARY KEY,
                         project_name VARCHAR(255),
                         customer_id INTEGER REFERENCES customer(id)
);