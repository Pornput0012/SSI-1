package ssi.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Integer id;
    private String nickName;
    private String email;
    private String fullName;
    private String phoneNumber;
    @JsonIgnore
    private String  status;
    private String userType;

    public Boolean getIsActive() {
        return status.equals("ACTIVE") ? true : false;
    }
}
