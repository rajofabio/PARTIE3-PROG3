insert into dish(id,name, price) values (1, 'Hot dog', '15000')
on conflict do nothing;
SELECT * FROM dish;
SELECT id, name, price FROM dish WHERE id = 1;

SELECT id_dish, quantity FROM dish_order WHERE id_order = 1;
