import edu.restaurant.app.dao.entity.Order;
import edu.restaurant.app.dao.operations.OrderCrudOperations;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderCrudOperationsTest {
    @Test
    public void testFindById() {
        OrderCrudOperations orderCrudOperations = new OrderCrudOperations();

        Order order = orderCrudOperations.findById(1L);
        assertNotNull(order);
        assertEquals("REF123", order.getReference());
    }
}