package ssi.backend.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String email;

    @NotNull
    @NotEmpty
    @Size(max = 14)
    private String password;



}
