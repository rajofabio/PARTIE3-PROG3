CREATE TABLE IF NOT EXISTS dish_order_status (
                                                 id BIGSERIAL PRIMARY KEY, -- Identifiant unique du statut du plat commandé
                                                 id_dish_order BIGINT NOT NULL REFERENCES dish_order(id), -- Clé étrangère vers la table DishOrder
                                                 status order_process_status NOT NULL, -- Statut du plat commandé
                                                 status_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL -- Date et heure du statut
);