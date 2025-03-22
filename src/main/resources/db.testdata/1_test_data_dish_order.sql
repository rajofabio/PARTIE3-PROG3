-- Insertion de plats commandés de test (uniquement des Hot Dogs)
INSERT INTO dish_order (id, id_order, id_dish, quantity)
VALUES
    (1, 1, 1, 2), -- Commande 1 : 2 Hot Dogs
    (2, 2, 1, 3); -- Commande 2 : 3 Hot Dogs
SELECT * FROM dish_order WHERE id_order = 1;
INSERT INTO dish_order (id, id_order, id_dish, quantity)
VALUES (3, 1, 1, 1)
ON CONFLICT (id) DO NOTHING;

UPDATE dish_order SET quantity = 2 WHERE id_order = 1 AND id_dish = 1;
SELECT * FROM dish_order WHERE id_order = 1;