package ssi.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerOrderDTO {
    private Integer id;
    private String email;
    private String fullName;
    private String userType;
    private String nickName;
}
