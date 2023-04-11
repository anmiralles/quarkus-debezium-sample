package me.amiralles.order.rest;

import me.amiralles.order.model.PurchaseOrder;
import me.amiralles.order.service.OrderService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;

    @POST
    public OrderOperationResponse addOrder(CreateOrderRequest createOrderRequest) {
        PurchaseOrder purchaseOrder = createOrderRequest.toOrder();
        purchaseOrder = orderService.addOrder(purchaseOrder);
        return OrderOperationResponse.from(purchaseOrder);
    }

    @PUT
    @Path("/{orderId}/lines/{orderLineId}")
    public OrderOperationResponse updateOrderLine(@PathParam("orderId") long orderId,
                                                  @PathParam("orderLineId") long orderLineId,
                                                  UpdateOrderLineRequest request) {
        PurchaseOrder updated = orderService.updateOrderLine(orderId, orderLineId, request.getNewStatus());
        return OrderOperationResponse.from(updated);
    }

}
