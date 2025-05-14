package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductItemDTO {
    private Integer id;

    private String brandName;

    private String model;

    private Integer price;

    private Integer ramGb;

    private Integer storageGb;

    private String color;

    private BigDecimal screenSizeInch;
    private Integer quantity;

}
