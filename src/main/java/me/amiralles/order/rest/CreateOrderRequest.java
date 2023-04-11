package me.amiralles.order.rest;

import me.amiralles.order.model.OrderLine;
import me.amiralles.order.model.PurchaseOrder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CreateOrderRequest {

    private long customerId;
    private LocalDateTime orderDate;
    private List<OrderLineDto> lineItems;

    public CreateOrderRequest() {
        this.lineItems = new ArrayList<>();
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderLineDto> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineDto> lineItems) {
        this.lineItems = lineItems;
    }

    public PurchaseOrder toOrder() {
        List<OrderLine> lines = lineItems.stream()
                .map(l -> new OrderLine(l.getItem(), l.getQuantity(), l.getTotalPrice()))
                .collect(Collectors.toList());

        return new PurchaseOrder(customerId, orderDate, lines);
    }
}
