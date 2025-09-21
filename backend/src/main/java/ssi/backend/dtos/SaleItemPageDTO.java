package ssi.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class SaleItemPageDTO {
    private List<SaleItemResponseDTO> content;
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
