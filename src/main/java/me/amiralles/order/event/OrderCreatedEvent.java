package me.amiralles.order.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import me.amiralles.order.model.OrderLine;
import me.amiralles.order.model.PurchaseOrder;

import java.time.Instant;

public class OrderCreatedEvent implements ExportedEvent<String, JsonNode> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final long id;
    private final JsonNode order;
    private final Instant timestamp;

    private OrderCreatedEvent(long id, JsonNode order) {
        this.id = id;
        this.order = order;
        this.timestamp = Instant.now();
    }

    public static OrderCreatedEvent of(PurchaseOrder purchaseOrder) {
        ObjectNode asJson = mapper.createObjectNode()
                .put("id", purchaseOrder.getId())
                .put("customerId", purchaseOrder.getCustomerId())
                .put("orderDate", purchaseOrder.getOrderDate().toString());

        ArrayNode items = asJson.putArray("lineItems");

        for (OrderLine orderLine : purchaseOrder.getLineItems()) {
            ObjectNode lineAsJon = mapper.createObjectNode()
                    .put("id", orderLine.getId())
                    .put("item", orderLine.getItem())
                    .put("quantity", orderLine.getQuantity())
                    .put("totalPrice", orderLine.getTotalPrice())
                    .put("status", orderLine.getStatus().name());

            items.add(lineAsJon);
        }

        return new OrderCreatedEvent(purchaseOrder.getId(), asJson);
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(id);
    }

    @Override
    public String getAggregateType() {
        return "Order";
    }

    @Override
    public JsonNode getPayload() {
        return order;
    }

    @Override
    public String getType() {
        return "OrderCreated";
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
