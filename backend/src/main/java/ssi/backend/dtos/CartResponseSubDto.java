package ssi.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class CartResponseSubDto {

    private int id;
    private int userId;
    private String imageName;
    private int productId;
    private String productModel;
    private String productBrandName;
    private Integer productStorageGb;
    private BigDecimal productScreenSizeInch;
    private String productColor;
    private int productQuantity;
    private int quantity;
    private String productSellerNickname;
    private Integer productSellerId;
    private Integer productPrice;


}
