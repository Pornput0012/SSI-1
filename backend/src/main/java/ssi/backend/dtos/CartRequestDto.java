package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartRequestDto {

    private int userId;
    private int productId;
    private String imageFileName;
    private int quantity;

}
