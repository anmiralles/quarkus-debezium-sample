package me.amiralles.order.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.amiralles.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class OrderEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventHandler.class);

    @Inject
    OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void onOrderEvent(UUID eventId, String eventType, String key, String event, Instant ts) {
        JsonNode eventPayload = deserialize(event);

        LOGGER.info("Received 'Order' event -- key: {}, event id: '{}', event type: '{}', ts: '{}'", key, eventId, eventType, ts);

        if (eventType.equals("OrderCreated")) {
            orderService.orderCreated(eventPayload);
        }
        else if (eventType.equals("OrderLineUpdated")) {
            orderService.orderLineUpdated(eventPayload);
        }
        else {
            LOGGER.warn("Unkown event type");
        }
    }

    private JsonNode deserialize(String event) {
        JsonNode eventPayload;

        try {
            String unescaped = objectMapper.readValue(event, String.class);
            eventPayload = objectMapper.readTree(unescaped);
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't deserialize event", e);
        }

        return eventPayload.has("schema") ? eventPayload.get("payload") : eventPayload;
    }
}
