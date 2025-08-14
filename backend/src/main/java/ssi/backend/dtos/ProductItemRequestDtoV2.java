package ssi.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductItemRequestDtoV2 {
    private Integer id;

    @NotNull(message = "Model is required")
    @NotEmpty
    @Size(max = 60)
    private String model;


    private Integer brandId;

    @NotNull(message = "Description is required")
    @NotEmpty
    private String description;

    @NotNull(message = "Price is required")
    private Integer price;

    private Integer ramGb;

    private BigDecimal screenSizeInch;

    private Integer storageGb;

    private String color;

    private Integer quantity;

    public void setModel(String model) {
        this.model = model.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public void setColor(String color) {
        this.color = color.trim() == "" ? null : color.trim();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = (quantity == null || quantity < 0) ? 1 : quantity;
    }
}
