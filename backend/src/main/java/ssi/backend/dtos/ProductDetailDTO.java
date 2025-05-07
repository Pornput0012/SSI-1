package ssi.backend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailDTO {
    private Integer id;

    @NotNull(message = "Model is required")
    private String model;

    private String brandName;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Integer price;

    private Integer ramGb;

    private Double screenSizeInch;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;

    private Integer storageGb;

    private String color;
}
