package ssi.backend.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductItemRequestDTO {

    private Integer id;

    @NotNull(message = "Model is required")
    @NotEmpty
    @Size(max = 60)
    private String model;

    @NotNull
    private BrandDTO brand;

    @NotNull(message = "Description is required")
    @NotEmpty
    private String description;

    @NotNull(message = "Price is required")
    private Integer price;

    private Integer ramGb;

    private BigDecimal screenSizeInch;

    private Integer storageGb;

    private String color;

    private Integer quantity;

    public void setModel(String model) {
        this.model = model.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public void setColor(String color) {
        this.color = (color == null || color.trim() == "") ? null : color.trim();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = (quantity == null || quantity < 0) ? 1 : quantity;
    }
}
