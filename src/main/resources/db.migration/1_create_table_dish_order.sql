CREATE TABLE IF NOT EXISTS dish_order (
                                          id BIGSERIAL PRIMARY KEY, -- Identifiant unique du plat commandé
                                          id_order BIGINT NOT NULL REFERENCES "order"(id), -- Clé étrangère vers la table Order
                                          id_dish BIGINT NOT NULL REFERENCES dish(id), -- Clé étrangère vers la table Dish
                                          quantity DOUBLE PRECISION NOT NULL, -- Quantité du plat commandé
                                          CHECK (quantity > 0) -- Vérifie que la quantité est positive
);