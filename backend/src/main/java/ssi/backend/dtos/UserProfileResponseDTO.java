package ssi.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserProfileResponseDTO {
    private Integer id;
    private String nickName;
    private String email;
    private String fullName;
    private String userType;
    private String phoneNumber;
    private String bankAccount;
    private String bankName;
    private String nationalIdNumber;
    private String nationalIdPhotoFront;
    private String nationalIdPhotoBack;
}
