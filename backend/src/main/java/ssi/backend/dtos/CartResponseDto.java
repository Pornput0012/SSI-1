package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;
import ssi.backend.entities.Cart;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartResponseDto {
    private String productSellerName;
    private List<CartResponseSubDto> cart;

}
