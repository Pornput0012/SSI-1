package ssi.backend.dtos;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDetailResponseDTO {
    private Integer id;
    private Integer buyerId;
    private SellerDetailDTO seller;
    private Instant orderDate;
    private String shippingAddress;
    private String orderNote;
    private List<OrderItemDTO> orderItems;
    private String orderStatus;
}
