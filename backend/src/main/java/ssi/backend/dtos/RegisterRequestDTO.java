package ssi.backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class RegisterRequestDTO {
    private Integer id = null;

    private String nickName;


    private String email;

    private String password;

    private String fullName;


    private String userType;


    // ฟิลด์ที่จำเป็นเฉพาะ Seller
    private String phoneNumber;
    private String bankAccount;
    private String bankName;
    private String nationalIdNumber;
}
