import edu.restaurant.app.dao.entity.*;
import edu.restaurant.app.dao.operations.OrderCrudOperations;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderManagementTest {

    private final OrderCrudOperations orderCrudOperations = new OrderCrudOperations();

    // Test 1 : Récupération d'une commande par son ID
    @Test
    public void testFindOrderById() {
        Order order = orderCrudOperations.findById(1L);
        assertNotNull(order);
        assertEquals("REF123", order.getReference());
    }

    // Test 2 : Vérification du statut actuel d'une commande
    @Test
    public void testGetActualStatus() {
        Order order = orderCrudOperations.findById(1L);
        assertEquals("CREATED", order.getActualStatus().toString());
    }

    // Test 3 : Vérification des plats dans une commande
    @Test
    public void testGetDishOrders() {
        Order order = orderCrudOperations.findById(1L);

        assertFalse(order.getDishOrders().isEmpty(), "La commande ne contient aucun plat !");
        assertEquals("Hot dog", order.getDishOrders().get(0).getDish().getName());

        assertEquals(2, order.getDishOrders().get(0).getQuantity());
    }

    // Test 4 : Calcul du montant total d'une commande
    @Test
    public void testGetTotalAmount() {
        Order order = orderCrudOperations.findById(1L);

        double expectedTotal = (15000.0 * 2) + (15000.0 * 2); // 2 Hot Dogs à 15 000 et 1 Omelette à 10 000
        assertEquals(expectedTotal, order.getTotalAmount());
    }

    // Test 5 : Transition des statuts d'une commande
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