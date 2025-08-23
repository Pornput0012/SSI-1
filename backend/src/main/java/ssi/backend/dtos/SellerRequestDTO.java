package ssi.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerRequestDTO {
    private Integer id;

    private String nickname;
    
    @Email
    private String email;
    
    private String password;
    
    @Size(min=4, max=40)
    private String fullname;

    private String mobileNumber;

    private String bankAccountNumber;

    private String bankName;

    private String nationalIdNumber;
}
