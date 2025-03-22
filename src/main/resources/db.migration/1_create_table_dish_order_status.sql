CREATE TABLE IF NOT EXISTS dish_order_status (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 id_dish_order BIGINT NOT NULL REFERENCES dish_order(id),
                                                 status order_process_status NOT NULL,
                                                 status_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL
);