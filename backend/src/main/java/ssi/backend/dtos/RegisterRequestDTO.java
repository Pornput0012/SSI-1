package ssi.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String nickname;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Size(min = 4, max = 40, message = "Fullname must be between 4 and 40 characters")
    private String fullname;

    // ใช้บอกว่า user ต้องการสมัครเป็น Buyer หรือ Seller
    private String accountType; // "BUYER" or "SELLER"

    // ฟิลด์ที่จำเป็นเฉพาะ Seller
    private String mobileNumber;

    private String bankAccountNumber;

    private String bankName;

    private String nationalIdNumber;
}
