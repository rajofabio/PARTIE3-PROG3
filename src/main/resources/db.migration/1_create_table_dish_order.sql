CREATE TABLE IF NOT EXISTS dish_order (
                                          id BIGSERIAL PRIMARY KEY,
                                          id_order BIGINT NOT NULL REFERENCES "order"(id),
                                          id_dish BIGINT NOT NULL REFERENCES dish(id),
                                          quantity DOUBLE PRECISION NOT NULL,
                                          CHECK (quantity > 0)
);