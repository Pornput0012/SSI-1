package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Integer id;
    private String nickName;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Boolean isActive;
    private String userType;
}
