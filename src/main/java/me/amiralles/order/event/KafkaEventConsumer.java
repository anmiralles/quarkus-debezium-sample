package me.amiralles.order.event;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.apache.kafka.common.header.Header;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class KafkaEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventConsumer.class);

    @Inject
    OrderEventHandler orderEventHandler;

    @Inject
    Tracer tracer;

    @Incoming("orders")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> onMessage(KafkaRecord<String, String> message) throws IOException {
        return CompletableFuture.runAsync(() -> {
                final Tracer.SpanBuilder spanBuilder = tracer.buildSpan("orders")
                        .asChildOf(TracingKafkaUtils.extractSpanContext(message.getHeaders(), tracer));
                try (final Scope span = tracer.scopeManager().activate(spanBuilder.start())) {
                    LOG.debug("Kafka message with key = {} arrived", message.getKey());
    
                    String eventId = getHeaderAsString(message, "id");
                    String eventType = getHeaderAsString(message, "eventType");
    
                    orderEventHandler.onOrderEvent(
                            UUID.fromString(eventId),
                            eventType,
                            message.getKey(),
                            message.getPayload(),
                            message.getTimestamp()
                    );

                    message.ack();
                }
                catch (Exception e) {
                    LOG.error("Error while preparing shipment");
                    throw e;
                }
        });
    }

    private String getHeaderAsString(KafkaRecord<?, ?> record, String name) {
        Header header = record.getHeaders().lastHeader(name);
        if (header == null) {
            throw new IllegalArgumentException("Expected record header '" + name + "' not present");
        }

        return new String(header.value(), Charset.forName("UTF-8"));
    }
}
