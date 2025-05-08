package ssi.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi.backend.dtos.ProductDetailDTO;
import ssi.backend.dtos.ProductItemDTO;
import ssi.backend.dtos.ProductItemRequestDTO;
import ssi.backend.dtos.ProductItemResponseDTO;
import ssi.backend.entities.Product;
import ssi.backend.services.ProductService;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop/v1/sale-items")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public ResponseEntity<List<ProductItemDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    @GetMapping("/hi")
    public ResponseEntity<List<Product>> getAll2Products() {
        return ResponseEntity.ok(productService.getAll2Products());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductDetailDTO productDetailDTO) {
        ProductDetailDTO updated = productService.updateProduct(id, productDetailDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Integer id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("")
    public ResponseEntity<ProductItemResponseDTO> createProduct(@Valid @RequestBody ProductItemRequestDTO product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product));
    }
}
