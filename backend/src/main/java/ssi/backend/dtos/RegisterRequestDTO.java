package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String userType; // "BUYER" หรือ "SELLER"
    private String nickname;
    private String email;
    private String password;
    private String fullname;
}
