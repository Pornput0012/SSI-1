package ssi.backend.dtos;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ssi.backend.entities.Brand;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductItemRequestDTO {

    @NotNull(message = "Model is required")
    @NotEmpty
    @Size(max = 60)
    private String model;

    @NotNull
    private BrandDTO brand;

    @NotNull(message = "Description is required")
    @NotEmpty
    private String description;

    @NotNull(message = "Price is required")
    private Integer price;

    private Integer ramGb;

    private BigDecimal screenSizeInch;

    private Integer storageGb;

    private String color;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    public void setModel(String model) {
        this.model = model.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public void setColor(String color) {
        this.color = color.trim();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = (quantity == null || quantity < 1) ? 1 : quantity;
    }
}
