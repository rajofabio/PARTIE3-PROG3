import edu.restaurant.app.dao.entity.*;
import edu.restaurant.app.dao.operations.DishOrderCrudOperations;
import edu.restaurant.app.dao.operations.OrderCrudOperations;
import org.junit.jupiter.api.Test;
import edu.restaurant.app.dao.operations.DishCrudOperations;

import java.time.Instant;
import java.util.List;

import static edu.restaurant.app.dao.entity.OrderProcessStatus.CREATED;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;

public class OrderManagementTest {

    private final OrderCrudOperations orderCrudOperations = new OrderCrudOperations();
    private final DishOrderCrudOperations dishOrderCrudOperations = new DishOrderCrudOperations();
    private final DishCrudOperations dishCrudOperations = new DishCrudOperations();

    @Test
    public void testFindOrderById() {
        Order order = orderCrudOperations.findById(1L);
        assertNotNull(order);
        assertEquals("REF123", order.getReference());
    }

    @Test
    public void testGetActualStatus() {
        orderCrudOperations.resetOrderStatus(1L, CREATED);
        Order order = orderCrudOperations.findById(1L);
        assertEquals("CREATED", order.getActualStatus().toString());
    }

    @Test
    public void testGetDishOrders() {
        Order order = orderCrudOperations.findById(1L);

        assertFalse(order.getDishOrders().isEmpty(), "La commande ne contient aucun plat !");
        assertEquals("Hot dog", order.getDishOrders().get(0).getDish().getName());

        assertEquals(2, order.getDishOrders().get(0).getQuantity());
    }

    @Test
    public void testGetTotalAmount() {
        Order order = orderCrudOperations.findById(1L);

        double expectedTotal = (15000.0 * 4);
        assertEquals(expectedTotal, order.getTotalAmount());
    }

    @Test
    public void testOrderStatusTransition() {
        orderCrudOperations.resetOrderStatus(1L, CREATED);
        Order order = orderCrudOperations.findById(1L);
        assertEquals(CREATED, order.getActualStatus());

        order.updateStatus(OrderProcessStatus.CONFIRMED);
        assertEquals(OrderProcessStatus.CONFIRMED, order.getActualStatus());

        order.updateStatus(OrderProcessStatus.IN_PREPARATION);
        assertEquals(OrderProcessStatus.IN_PREPARATION, order.getActualStatus());

        order.updateStatus(OrderProcessStatus.COMPLETED);
        assertEquals(OrderProcessStatus.COMPLETED, order.getActualStatus());

        order.updateStatus(OrderProcessStatus.SERVED);
        assertEquals(OrderProcessStatus.SERVED, order.getActualStatus());
    }
    @Test
    void save_order() {
        Order order = new Order();


        Dish dish = dishCrudOperations.findById(1L);
        assertNotNull(dish, "Le plat doit exister avant de créer une commande");

        DishOrder dishOrder = new DishOrder();
        dishOrder.setDish(dish);
        dishOrder.setQuantity(1.0);
        dishOrder.setOrder(order);
        order.addDishOrder(dishOrder);

        OrderStatus status = new OrderStatus(order, OrderProcessStatus.CREATED, Instant.now());
        order.addOrderStatus(status);

        List<Order> actual = orderCrudOperations.saveAll(List.of(order));

        assertEquals(1, actual.size(), "Une seule commande doit être sauvegardée");
        Order actualOrder = actual.get(0);
        assertNotNull(actualOrder.getId());

        List<DishOrder> dishOrders = actualOrder.getDishOrders();
        assertNotNull(dishOrders);
        assertFalse(dishOrders.isEmpty());


        assertEquals(OrderProcessStatus.CREATED, actualOrder.getActualStatus());
    }
}

