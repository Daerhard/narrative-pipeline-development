CREATE TABLE "Customer" (
    id      BIGSERIAL    PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    zipcode INTEGER      NOT NULL,
    street  VARCHAR(255) NOT NULL,
    city    VARCHAR(255) NOT NULL
);
