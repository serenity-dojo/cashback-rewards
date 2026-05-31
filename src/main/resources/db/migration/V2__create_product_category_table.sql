CREATE TABLE product_category (
    mcc           VARCHAR(8)   PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    cashback_rate NUMERIC(6, 4) NOT NULL
);

CREATE TABLE default_cashback_rate (
    id   INTEGER       PRIMARY KEY,
    rate NUMERIC(6, 4) NOT NULL
);
