package ssi.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    @NotNull
    private String nickName;
    @NotNull
    private String fullName;
}
