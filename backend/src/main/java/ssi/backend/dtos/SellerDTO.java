package ssi.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SellerDTO {
    private Integer id;
    @JsonProperty("userName")
    private String nickname;


}
