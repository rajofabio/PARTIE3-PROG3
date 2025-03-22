CREATE TABLE IF NOT EXISTS "order" (
                                       id BIGSERIAL PRIMARY KEY, -- Identifiant unique de la commande
                                       reference VARCHAR(50) UNIQUE NOT NULL, -- Référence unique de la commande
                                       creation_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL -- Date et heure de création de la commande
);