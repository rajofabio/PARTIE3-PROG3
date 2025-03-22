INSERT INTO "order" (id, reference, creation_datetime)
VALUES
    (1, 'REF123', '2025-02-01 10:00:00'),
    (2, 'REF456', '2025-02-02 12:00:00')
ON CONFLICT (id) DO NOTHING;