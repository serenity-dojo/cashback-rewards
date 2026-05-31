CREATE TABLE merchant (
    normalized_name VARCHAR(255) PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    partner         BOOLEAN      NOT NULL
);
