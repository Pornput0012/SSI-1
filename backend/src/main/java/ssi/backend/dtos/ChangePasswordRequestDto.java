package ssi.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDto {
    @NotEmpty
    private String currentPassword;
    @NotEmpty
    private String newPassword;
}
