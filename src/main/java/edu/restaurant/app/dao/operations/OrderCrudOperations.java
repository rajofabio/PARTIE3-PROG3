package edu.restaurant.app.dao.operations;

import edu.restaurant.app.dao.DataSource;
import edu.restaurant.app.dao.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderCrudOperations implements CrudOperations<Order> {
    private final DataSource dataSource = new DataSource();
    private final DishOrderCrudOperations dishOrderCrudOperations = new DishOrderCrudOperations();

    @Override
    public List<Order> getAll(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Order findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, reference, creation_datetime FROM \"order\" WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Order order = new Order();
                    order.setId(resultSet.getLong("id"));
                    order.setReference(resultSet.getString("reference"));
                    order.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());

                    // Récupérer les plats commandés associés à cette commande
                    List<DishOrder> dishOrders = dishOrderCrudOperations.findByOrderId(id);
                    order.setDishOrders(dishOrders);

                    // Récupérer les statuts de la commande
                    order.setOrderStatuses(findOrderStatuses(order.getId()));

                    return order;
                }
                throw new RuntimeException("Order.id=" + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> saveAll(List<Order> entities) {
        return List.of();
    }

    private List<OrderStatus> findOrderStatuses(Long orderId) {
        List<OrderStatus> statuses = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, status, status_datetime FROM order_status WHERE id_order = ?")) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    statuses.add(new OrderStatus(
                            resultSet.getLong("id"),
                            new Order(orderId),
                            OrderProcessStatus.valueOf(resultSet.getString("status")),
                            resultSet.getTimestamp("status_datetime").toInstant()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return statuses;
    }
    public void resetOrderStatus(Long orderId, OrderProcessStatus newStatus) {
        String sql = "INSERT INTO order_status (id_order, status, status_datetime) " +
                "VALUES (?, CAST(? AS order_process_status), now())";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            statement.setString(2, newStatus.name()); // S'assure que le statut est converti correctement
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la commande", e);
        }
    }


}
