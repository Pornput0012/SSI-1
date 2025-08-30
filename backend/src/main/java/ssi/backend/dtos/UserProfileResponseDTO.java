package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponseDTO {
    private Integer id;
    private String nickName;
    private String email;
    private String fullName;
}
