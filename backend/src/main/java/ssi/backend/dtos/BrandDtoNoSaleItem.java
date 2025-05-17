package ssi.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ssi.backend.entities.Product;

import java.util.Set;

@Getter
@Setter
public class BrandDtoNoSaleItem {
    private Integer id;
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;
    private Integer noOfSaleItems;

    @JsonIgnore
    private Set<Product> products;


    public Integer getNoOfSaleItems() {
        return products == null ? 0 : products.size();
    }
}
