package ssi.backend.dtos;


import lombok.Getter;
import lombok.Setter;
import ssi.backend.entities.Product;

@Getter
@Setter
public class SaleItemImageDto {
    private String fileName;
    private Integer imageViewOrder;
    private Product product;
}
