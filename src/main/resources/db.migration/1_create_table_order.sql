CREATE TABLE IF NOT EXISTS "order" (
                                       id BIGSERIAL PRIMARY KEY,
                                       reference VARCHAR(50) UNIQUE NOT NULL,
                                       creation_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL
);