package ssi.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BrandDTO {

    private Integer id;

    @NotNull
    @NotEmpty
    private String name;

    private Boolean isActive;

}
