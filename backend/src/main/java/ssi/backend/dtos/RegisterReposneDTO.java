package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterReposneDTO {
    private Integer id;
    private String userType;
    private String nickname;
    private String email;
    private String fullname;
}
