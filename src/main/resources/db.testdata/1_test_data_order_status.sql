-- Insertion de statuts de commande de test
INSERT INTO order_status (id_order, status, status_datetime)
VALUES
    (1, 'CREATED', '2025-02-01 10:00:00'),
    (1, 'CONFIRMED', '2025-02-01 10:05:00'),
    (2, 'CREATED', '2025-02-02 12:00:00');
SELECT * FROM order_status WHERE id_order = 1 ORDER BY status_datetime DESC;

