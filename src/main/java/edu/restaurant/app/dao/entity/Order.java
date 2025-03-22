package edu.restaurant.app.dao.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@ToString(exclude = {"dishOrders", "orderStatuses"})
@Data
@NoArgsConstructor
public class Order {
    public Order(Long id, String reference, Instant creationDatetime, List<DishOrder> dishOrders, List<OrderStatus> orderStatuses) {
        this.id = id;
        this.reference = reference;
        this.creationDatetime = creationDatetime;
        this.dishOrders = dishOrders;
        this.orderStatuses = orderStatuses;
    }

    private Long id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders = new ArrayList<>();
    private List<OrderStatus> orderStatuses = new ArrayList<>();

    public Order(Long id) {
        this.id = id;
    }


    public void addDishOrder(DishOrder dishOrder) {
        dishOrder.setOrder(this); // 🚀 S'assurer que le DishOrder est bien lié à cette commande
        this.dishOrders.add(dishOrder);
    }


    public OrderProcessStatus getActualStatus() {
        if (orderStatuses.isEmpty()) {
            throw new IllegalStateException("Aucun statut trouvé pour cette commande.");
        }

        return orderStatuses.stream()
                .filter(orderStatus -> orderStatus.getStatusDatetime() != null)
                .max(Comparator.comparing(OrderStatus::getStatusDatetime))
                .map(OrderStatus::getStatus)
                .orElseThrow(() -> new IllegalStateException("Aucun statut valide trouvé pour cette commande."));
    }

    private void validateStatusTransition(OrderProcessStatus newStatus) {
        if (orderStatuses.isEmpty()) {
            if (newStatus != OrderProcessStatus.CREATED) {
                throw new IllegalStateException("Le premier statut doit être CREATED");
            }
            return;
        }

        OrderProcessStatus currentStatus = getActualStatus();


        switch (currentStatus) {
            case CREATED:
                if (newStatus != OrderProcessStatus.CONFIRMED) {
                    throw new IllegalStateException("Transition invalide : CREATED -> " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderProcessStatus.IN_PREPARATION) {
                    throw new IllegalStateException("Transition invalide : CONFIRMED -> " + newStatus);
                }
                break;
            case IN_PREPARATION:
                if (newStatus != OrderProcessStatus.COMPLETED) {
                    throw new IllegalStateException("Transition invalide : IN_PREPARATION -> " + newStatus);
                }
                break;
            case COMPLETED:
                if (newStatus != OrderProcessStatus.SERVED) {
                    throw new IllegalStateException("Transition invalide : COMPLETED -> " + newStatus);
                }
                break;
            case SERVED:
                throw new IllegalStateException("Aucune transition possible après SERVED");
            default:
                throw new IllegalStateException("Statut actuel inconnu : " + currentStatus);
        }
    }

    public void updateStatus(OrderProcessStatus newStatus) {

        if (!isValidTransition(this.getActualStatus(), newStatus)) {
            throw new IllegalStateException("Transition invalide de " + this.getActualStatus() + " à " + newStatus);
        }
        this.orderStatuses.add(new OrderStatus(this, newStatus, Instant.now()));
    }

    private boolean isValidTransition(OrderProcessStatus currentStatus, OrderProcessStatus newStatus) {
        return switch (currentStatus) {
            case CREATED -> newStatus == OrderProcessStatus.CONFIRMED;
            case CONFIRMED -> newStatus == OrderProcessStatus.IN_PREPARATION;
            case IN_PREPARATION -> newStatus == OrderProcessStatus.COMPLETED;
            case COMPLETED -> newStatus == OrderProcessStatus.SERVED;
            case SERVED -> false;
        };
    }


    public void addOrderStatus(OrderStatus orderStatus) {
        validateStatusTransition(orderStatus.getStatus());
        orderStatuses.add(orderStatus);
    }

    public double getTotalAmount() {
        double totalAmount = 0.0;
        for (DishOrder dishOrder : this.dishOrders) {
            Dish dish = dishOrder.getDish();
            int quantity = (int) dishOrder.getQuantity();
            totalAmount += dish.getPrice() * quantity;
        }

        return totalAmount;
    }

}