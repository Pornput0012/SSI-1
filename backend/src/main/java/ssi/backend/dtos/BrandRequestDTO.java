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
    @NotEmpty
    private String websiteUrl;

    @Size(max = 80)
    @NotEmpty
    private String countryOfOrigin;

    private Boolean isActive;

    public void setActive(Boolean active) {
        this.isActive = (active == null) ? false : active;
    }
}
