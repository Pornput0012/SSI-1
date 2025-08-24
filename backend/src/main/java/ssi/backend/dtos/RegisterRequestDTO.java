package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String nickname;
    private String email;
    private String password;
    private String fullname;

    // ใช้บอกว่า user ต้องการสมัครเป็น Buyer หรือ Seller
    private String accountType; // "BUYER" or "SELLER"

    // ฟิลด์ที่จำเป็นเฉพาะ Seller
    private String mobileNumber;
    private String bankAccountNumber;
    private String bankName;
    private String nationalIdNumber;
}
