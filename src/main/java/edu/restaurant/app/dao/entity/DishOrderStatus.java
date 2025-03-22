package edu.restaurant.app.dao.entity;


import java.time.Instant;

public class DishOrderStatus {
    private Long id;
    private DishOrder dishOrder;
    private OrderProcessStatus status;
    private Instant statusDatetime;


    public DishOrderStatus() {}

    public DishOrderStatus(Long id, DishOrder dishOrder, OrderProcessStatus status, Instant statusDatetime) {
        this.id = id;
        this.dishOrder = dishOrder;
        this.status = status;
        this.statusDatetime = statusDatetime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DishOrder getDishOrder() {
        return dishOrder;
    }

    public void setDishOrder(DishOrder dishOrder) {
        this.dishOrder = dishOrder;
    }

    public OrderProcessStatus getStatus() {
        return status;
    }

    public void setStatus(OrderProcessStatus status) {
        this.status = status;
    }

    public Instant getStatusDatetime() {
        return statusDatetime;
    }

    public void setStatusDatetime(Instant statusDatetime) {
        this.statusDatetime = statusDatetime;
    }
}