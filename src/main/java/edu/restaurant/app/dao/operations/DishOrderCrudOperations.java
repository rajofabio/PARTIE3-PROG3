package edu.restaurant.app.dao.operations;

import edu.restaurant.app.dao.DataSource;
import edu.restaurant.app.dao.entity.*;
import lombok.SneakyThrows;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DishOrderCrudOperations implements CrudOperations<DishOrder> {
    private final DataSource dataSource = new DataSource();

    @Override
    public List<DishOrder> getAll(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DishOrder findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, id_order, id_dish, quantity FROM dish_order WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setId(resultSet.getLong("id"));
                    dishOrder.setOrder(new Order(resultSet.getLong("id_order")));
                    dishOrder.setDish(new Dish(resultSet.getLong("id_dish")));
                    dishOrder.setQuantity(resultSet.getDouble("quantity"));
                    return dishOrder;
                }
                throw new RuntimeException("DishOrder.id=" + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public List<DishOrder> saveAll(List<DishOrder> entities) {
        List<DishOrder> dishOrders = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO dish_order (id, id_order, id_dish, quantity) VALUES (?, ?, ?, ?)"
                             + " ON CONFLICT (id) DO UPDATE SET id_order = excluded.id_order, id_dish = excluded.id_dish, quantity = excluded.quantity"
                             + " RETURNING id, id_order, id_dish, quantity")) {
            for (DishOrder dishOrder : entities) {
                statement.setLong(1, dishOrder.getId());
                statement.setLong(2, dishOrder.getOrder().getId());
                statement.setLong(3, dishOrder.getDish().getId());
                statement.setDouble(4, dishOrder.getQuantity());
                statement.addBatch();
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    DishOrder savedDishOrder = new DishOrder();
                    savedDishOrder.setId(resultSet.getLong("id"));
                    savedDishOrder.setOrder(new Order(resultSet.getLong("id_order")));
                    savedDishOrder.setDish(new Dish(resultSet.getLong("id_dish")));
                    savedDishOrder.setQuantity(resultSet.getDouble("quantity"));
                    dishOrders.add(savedDishOrder);
                }
            }
        }
        return dishOrders;
    }

    public List<DishOrder> findByOrderId(Long orderId) {
        List<DishOrder> dishOrders = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT d_s.id, d_s.name, d_s.price, d_o.id AS dish_order_id, d_o.quantity " +
                             "FROM dish_order AS d_o " +
                             "JOIN dish AS d_s ON d_o.id_dish = d_s.id " +
                             "WHERE d_o.id_order = ?")) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Dish dish = new Dish();
                    dish.setId(resultSet.getLong("id"));
                    dish.setName(resultSet.getString("name"));

                    double price = resultSet.getDouble("price");
                    if (resultSet.wasNull()) {
                        throw new RuntimeException("Le prix du plat avec id=" + dish.getId() + " est NULL !");
                    }
                    dish.setPrice(price);

                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setId(resultSet.getLong("dish_order_id"));
                    dishOrder.setDish(dish);
                    dishOrder.setQuantity(resultSet.getDouble("quantity"));
                    dishOrders.add(dishOrder);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Nombre de plats récupérés pour orderId=" + orderId + " : " + dishOrders.size());
        return dishOrders;
    }


}