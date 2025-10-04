package ssi.backend.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderBuyerPageDTO {
    private List<OrderResponseDTO> content;
    private Boolean last;
    private Boolean first;

    private Integer totalPages;
    private Integer totalElements;

    private Integer size;
    private String sort;

    @JsonIgnore
    private Integer number;

    public Integer getPage() {
        return this.number;
    }
    
}
