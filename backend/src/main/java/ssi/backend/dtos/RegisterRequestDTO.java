package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private Integer id;
    private String nickName;
    private String email;
    private String password;
    private String fullName;

    private String userType;


    // ฟิลด์ที่จำเป็นเฉพาะ Seller
    private String phoneNumber;
    private String bankAccountNumber;
    private String bankName;
    private String idCardNumber;
}
