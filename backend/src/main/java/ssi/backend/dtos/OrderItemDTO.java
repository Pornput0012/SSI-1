package ssi.backend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Integer no;

    @NotNull(message = "Sale item ID is required")
    private Integer saleItemId;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Integer price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String description;
}
