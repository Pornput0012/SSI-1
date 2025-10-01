package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Integer no;
    private Integer saleItemId;
    private Integer price;
    private Integer quantity;
    private String description;
}
