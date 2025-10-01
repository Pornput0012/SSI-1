package ssi.backend.dtos;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {
    private Integer id;
    private Integer buyerId;
    private SellerOrderDTO seller;
    private Instant orderDate;
    private String shippingAddress;
    private String orderNote;
    private List<OrderItemDTO> orderItems;
    private String orderStatus;
}
