package ssi.backend.dtos;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderSellerResponseDTO {
    private Integer id;
    private BuyerDTO buyer;
    private Integer sellerId;
    private Instant orderDate;
    private Instant paymentDate;
    private String shippingAddress;
    private String orderNote;
    private List<OrderItemDTO> orderItems;
    private String orderStatus;
    private Boolean viewOrder;

}
