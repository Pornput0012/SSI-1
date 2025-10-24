package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordResponseDto {
    private boolean success ;
    private String message;
}
