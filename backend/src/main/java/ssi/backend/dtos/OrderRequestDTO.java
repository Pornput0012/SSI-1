package ssi.backend.dtos;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {
    private Integer buyerId;
    private Integer sellerId;
    private Instant orderDate;
    private String shippingAddress;
    private String orderNote;
    private List<OrderItemDTO> orderItems;
    private String orderStatus;
}
