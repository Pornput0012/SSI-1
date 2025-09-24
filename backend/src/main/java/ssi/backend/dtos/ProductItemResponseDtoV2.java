package ssi.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ProductItemResponseDtoV2 {
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

    private List<SaleItemImageDtoResponse> saleItemImages;

    private Instant createdOn;

    private Instant updatedOn;

    private SellerDTO seller;
}
