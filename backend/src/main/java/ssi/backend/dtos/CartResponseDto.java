package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;
import ssi.backend.entities.Cart;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartResponseDto {
    private Integer productSellerId;
    private String productSellerNickname;
    private List<CartResponseSubDto> cart;

}
