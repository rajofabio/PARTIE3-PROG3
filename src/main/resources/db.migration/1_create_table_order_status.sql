DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'order_process_status') THEN
            CREATE TYPE order_process_status AS ENUM ('CREATED', 'CONFIRMED', 'IN_PREPARATION', 'COMPLETED', 'SERVED');
        END IF;
    END$$;


CREATE TABLE IF NOT EXISTS order_status (
                                            id BIGSERIAL PRIMARY KEY,
                                            id_order BIGINT NOT NULL REFERENCES "order"(id),
                                            status order_process_status NOT NULL,
                                            status_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL
);