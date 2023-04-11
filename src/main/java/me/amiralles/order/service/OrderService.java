package me.amiralles.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import me.amiralles.order.event.OrderCreatedEvent;
import me.amiralles.order.event.OrderLineUpdatedEvent;
import me.amiralles.order.exception.EntityNotFoundException;
import me.amiralles.order.model.PurchaseOrder;
import me.amiralles.order.model.OrderLineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ApplicationScoped
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    Event<ExportedEvent<?, ?>> event;

    @Transactional
    public PurchaseOrder addOrder(PurchaseOrder purchaseOrder) {
        purchaseOrder = entityManager.merge(purchaseOrder);

        // Fire events for newly created PurchaseOrder
        event.fire(OrderCreatedEvent.of(purchaseOrder));

        return purchaseOrder;
    }

    @Transactional
    public PurchaseOrder updateOrderLine(long orderId, long orderLineId, OrderLineStatus newStatus) {
        PurchaseOrder purchaseOrder = entityManager.find(PurchaseOrder.class, orderId);
        if (purchaseOrder == null) {
            throw new EntityNotFoundException("Order with id " + orderId + " could not be found");
        }

        OrderLineStatus oldStatus = purchaseOrder.updateOrderLine(orderLineId, newStatus);
        event.fire(OrderLineUpdatedEvent.of(orderId, orderLineId, newStatus, oldStatus));

        return purchaseOrder;
    }


    public void orderCreated(JsonNode event) {
        LOGGER.info("Processing 'OrderCreated' event: {}", event);
    }

    public void orderLineUpdated(JsonNode event) {
        LOGGER.info("Processing 'OrderLineUpdated' event: {}", event);
    }

}
