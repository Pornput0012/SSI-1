package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyDTO {
    private String email;
    private String userType;
    private Integer user_id;
    private String type;
}
