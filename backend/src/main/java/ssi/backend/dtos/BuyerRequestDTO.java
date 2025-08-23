// RegisterRequestDTO.java
package ssi.backend.dtos;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerRequestDTO {
    private Integer id;

    private String nickname;
    
    @Email
    private String email;
    
    private String password;
    
    private String fullname;
    
}
