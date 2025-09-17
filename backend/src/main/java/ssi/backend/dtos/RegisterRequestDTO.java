package ssi.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private Integer id = null;

    @NotNull
    private String nickName;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String fullName;

    @NotNull
    private String userType;


    // ฟิลด์ที่จำเป็นเฉพาะ Seller
    private String phoneNumber;
    private String bankAccount;
    private String bankName;
    private String nationalIdNumber;
}
