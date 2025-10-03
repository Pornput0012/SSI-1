package ssi.backend.dtos;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {
    @NotNull(message = "Buyer ID is required")
    private Integer buyerId;

    @NotNull(message = "Seller ID is required")
    private Integer sellerId;
    
    @NotNull(message = "Shipping address is required")
    private String shippingAddress;
    
    private String orderNote;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemDTO> orderItems;
    
    private String orderStatus;
}
