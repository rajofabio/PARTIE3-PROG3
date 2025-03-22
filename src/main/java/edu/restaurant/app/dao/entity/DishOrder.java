package edu.restaurant.app.dao.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
public class DishOrder {
    private Long id;
    private Order order;
    private Dish dish;
    private double quantity;
    private List<DishOrderStatus> dishOrderStatuses = new ArrayList<>();

    public DishOrder(Long id, Order order, Dish dish, double quantity) {
        this.id = id;
        this.order = order;
        this.dish = dish;
        this.quantity = quantity;
    }
}