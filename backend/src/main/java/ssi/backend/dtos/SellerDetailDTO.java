package ssi.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
public class SellerDetailDTO {
    private Integer id;
    private String email;
    private String fullName;
    private String userType;
    private String nickName;
}
