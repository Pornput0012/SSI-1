package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandResponseDTO {
    private Integer id;
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;
}
