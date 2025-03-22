import edu.restaurant.app.dao.entity.*;
import edu.restaurant.app.dao.operations.OrderCrudOperations;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderManagementTest {

    private final OrderCrudOperations orderCrudOperations = new OrderCrudOperations();

    @Test
    public void testFindOrderById() {
        Order order = orderCrudOperations.findById(1L);
        assertNotNull(order);
        assertEquals("REF123", order.getReference());
    }


    @Test
    public void testGetActualStatus() {
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

        double expectedTotal = (15000.0 * 2) + (15000.0 * 2); // 2 Hot Dogs à 15 000 et 1 Omelette à 10 000
        assertEquals(expectedTotal, order.getTotalAmount());
    }


    @Test
    public void testOrderStatusTransition() {
        Order order = orderCrudOperations.findById(1L);
        List<OrderStatus> statuses = order.getOrderStatuses();
        assertEquals("CREATED", statuses.get(0).getStatus().toString());
        assertEquals("CONFIRMED", statuses.get(1).getStatus().toString());
    }

    @Test
    public void testStockValidation() {
        orderCrudOperations.resetOrderStatus(1L, OrderProcessStatus.CREATED);
        Order order = orderCrudOperations.findById(1L);
        assertEquals(OrderProcessStatus.CREATED, order.getActualStatus(), "La commande n'est pas en statut CREATED !");
        assertDoesNotThrow(() -> order.confirm());
        assertEquals(OrderProcessStatus.CONFIRMED, order.getActualStatus());
    }


    @Test
  public void testConfirmAlreadyConfirmedOrder() {
        Order order = orderCrudOperations.findById(1L);
        if (order.getActualStatus() == OrderProcessStatus.CONFIRMED) {
            assertThrows(IllegalStateException.class, () -> order.confirm(),
                    "On ne doit pas pouvoir confirmer une commande déjà confirmée !");
        }
    }

}