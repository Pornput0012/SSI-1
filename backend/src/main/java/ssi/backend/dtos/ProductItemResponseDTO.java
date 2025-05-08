package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProductItemResponseDTO {

    private Integer id;

    private String model;

    private String brandName;

    private String description;

    private Integer price;

    private Integer ramGb;

    private Double screenSizeInch;

    private Integer quantity;

    private Integer storageGb;

    private String color;

    private Instant createdOn;

    private Instant updatedOn;
}
