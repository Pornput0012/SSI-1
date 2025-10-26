package ssi.backend.dtos;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDetailResponseDTO {
    private Integer id;
    private Integer buyerId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BuyerDTO buyer;
    private SellerDetailDTO seller;
    private Instant orderDate;
    private Instant paymentDate;
    private String shippingAddress;
    private String orderNote;
    private List<OrderItemDTO> orderItems;
    private String orderStatus;

}
