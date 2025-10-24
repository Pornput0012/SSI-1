package ssi.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequestDTO {
    @NotEmpty
    @Email
    private String email;
}
