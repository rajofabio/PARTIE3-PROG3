package edu.restaurant.app.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class OrderStatus {
    private Long id;
    private Order order;
    private OrderProcessStatus status;
    private Instant statusDatetime;

    public OrderStatus(Order order, OrderProcessStatus status, Instant statusDatetime) {
        this.order = order;
        this.status = status;
        this.statusDatetime = statusDatetime;
    }

    public OrderStatus(Long id, Order order, OrderProcessStatus status, Instant statusDatetime) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.statusDatetime = statusDatetime;
    }
}
