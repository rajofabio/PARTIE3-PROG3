    package edu.restaurant.app.dao.operations;
    
    import edu.restaurant.app.dao.DataSource;
    import edu.restaurant.app.dao.entity.*;
    import lombok.SneakyThrows;
    
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
    
    
                        List<DishOrder> dishOrders = dishOrderCrudOperations.findByOrderId(id);
                        order.setDishOrders(dishOrders);
    
    
                        order.setOrderStatuses(findOrderStatuses(order.getId()));
    
                        return order;
                    }
                    throw new RuntimeException("Order.id=" + id + " not found");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @SneakyThrows
        @Override
        public List<Order> saveAll(List<Order> entities) {
            List<Order> savedOrders = new ArrayList<>();
            String sql = "INSERT INTO \"order\" (reference, creation_datetime) VALUES (?, ?)"
                    + " ON CONFLICT (reference) DO UPDATE SET creation_datetime = excluded.creation_datetime"
                    + " RETURNING id, reference, creation_datetime";

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                for (Order order : entities) {
                    statement.setString(1, order.getReference());
                    statement.setTimestamp(2, Timestamp.from(order.getCreationDatetime()));
                    statement.addBatch();
                }

                int[] rows = statement.executeBatch();
                if (rows.length == 0) {
                    throw new RuntimeException("Aucune commande n'a été insérée !");
                }

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    while (resultSet.next()) {
                        Order savedOrder = new Order();
                        savedOrder.setId(resultSet.getLong("id"));
                        savedOrder.setReference(resultSet.getString("reference"));
                        savedOrder.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());

                        // 🚀 Sauvegarde des plats de la commande
                        List<DishOrder> dishOrders = entities.stream()
                                .filter(o -> o.getReference().equals(savedOrder.getReference()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Commande non trouvée"))
                                .getDishOrders();

                        if (!dishOrders.isEmpty()) {
                            dishOrderCrudOperations.saveAll(dishOrders);
                            savedOrder.setDishOrders(dishOrders);
                        }

                        savedOrders.add(savedOrder);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la sauvegarde des commandes : " + e.getMessage(), e);
            }
            return savedOrders;
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
                statement.setString(2, newStatus.name());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la mise à jour du statut de la commande", e);
            }
        }
    }
