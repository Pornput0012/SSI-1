package ssi.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandRequestDTO {

    @NotNull
    @NotEmpty
    @Size(max = 30)
    private String name;

    @Size(max = 40)
    private String websiteUrl;

    @Size(max = 80)
    private String countryOfOrigin;

    private Boolean isActive;

    public void setActive(Boolean active) {
        this.isActive = (active == null) ? false : active;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = (websiteUrl == "") ? null : websiteUrl;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = (countryOfOrigin == "") ? null : countryOfOrigin;
    }
}
