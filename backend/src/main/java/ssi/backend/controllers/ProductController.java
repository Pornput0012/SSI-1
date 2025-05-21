package ssi.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi.backend.dtos.*;
import ssi.backend.entities.Product;
import ssi.backend.services.ProductService;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/v1/sale-items")
    public ResponseEntity<List<ProductItemDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/v2/sale-items")
    public ResponseEntity<ProductPageDTO> getAllProductsV2(@RequestParam int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(required = false) String sortField,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection,
                                                           @RequestParam(defaultValue = "") List<String> filterBrands) {

        ProductPageDTO productPageDTO = productService.getAllProductsV2(page, size, sortField, sortDirection, filterBrands);
        return ResponseEntity.ok(productPageDTO);
    }


    @GetMapping("/v1/sale-items/{id}")
    public ResponseEntity<ProductDetailDTO> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/v1/sale-items/{id}")
    public ResponseEntity<ProductItemResponseDTO> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductItemRequestDTO productItemRequestDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productItemRequestDTO));
    }

    @DeleteMapping("/v1/sale-items/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Integer id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v1/sale-items")
    public ResponseEntity<ProductItemResponseDTO> createProduct(@Valid @RequestBody ProductItemRequestDTO product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product));
    }
}
