package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class CartResponseSubDto {

    private int id;
    private int userId;
    private int productId;
    private String productModel;
    private String productBrandName;
    private Integer productStorageGb;
    private BigDecimal productScreenSizeInch;
    private String productColor;
    private int quantity;
    private String productSellerName;
    private Integer productPrice;


}
