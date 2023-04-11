/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package me.amiralles.order.rest;

import me.amiralles.order.model.OrderLineStatus;

public class UpdateOrderLineRequest {

    private OrderLineStatus newStatus;

    public OrderLineStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderLineStatus newStatus) {
        this.newStatus = newStatus;
    }
}
