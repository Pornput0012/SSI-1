package ssi.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    @NotBlank(message = "Nickname is required")
    private String nickname;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Fullname is required")
    @Size(min = 4, max = 40, message = "Fullname must be between 4 and 40 characters")
    private String fullname;

    // ใช้บอกว่า user ต้องการสมัครเป็น Buyer หรือ Seller
    private String accountType; // "BUYER" or "SELLER"

    // ฟิลด์ที่จำเป็นเฉพาะ Seller
    @NotBlank(message = "mobileNumber is required for seller")
    private String mobileNumber;

    @NotBlank(message = "bankAccountNumber is required for seller")
    private String bankAccountNumber;

    @NotBlank(message = "bankName is required for seller")
    private String bankName;

    @NotBlank(message = "nationalIdNumber is required for seller")
    private String nationalIdNumber;
}
