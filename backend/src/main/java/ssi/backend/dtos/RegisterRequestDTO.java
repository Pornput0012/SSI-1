package ssi.backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class RegisterRequestDTO {
    private Integer id = null;

    @NotNull
    private String nickName;

    @NotNull
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Invalid email format"
    )
    private String email;

    @NotNull
    @Length(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$", message = "Password must contain at least one lowercase, one uppercase, one number, one special character, and be 8+ characters long"
    )
    private String password;

    @NotNull
    @Length(min = 4, max = 40)
    private String fullName;

    @NotNull
    private String userType;


    // ฟิลด์ที่จำเป็นเฉพาะ Seller
    private String phoneNumber;
    private String bankAccount;
    private String bankName;
    private String nationalIdNumber;
}
