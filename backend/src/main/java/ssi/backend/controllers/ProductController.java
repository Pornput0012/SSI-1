package ssi.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ssi.backend.dtos.*;
import ssi.backend.services.FileService;
import ssi.backend.services.ProductService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;

    @GetMapping("/v1/sale-items")
    public ResponseEntity<List<ProductItemDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/v2/sale-items")
    public ResponseEntity<ProductPageDtoV2> getAllProductsV2(
            @RequestParam int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) List<String> filterBrands,
            @RequestParam(required = false) List<Integer> filterStorages,
            @RequestParam(required = false) Integer filterPriceLower,
            @RequestParam(required = false) Integer filterPriceUpper,
            @RequestParam(required = false) String searchKeyWord) {

        ProductPageDtoV2 productPageDTO = productService.getAllProductsV2(page, size, sortField, sortDirection,
                filterBrands, filterStorages, filterPriceLower, filterPriceUpper, searchKeyWord);
        return ResponseEntity.ok(productPageDTO);
    }

    @GetMapping("/v1/sale-items/{id}")
    public ResponseEntity<ProductDetailDTO> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/v2/sale-items/{id}")
    public ResponseEntity<ProductItemResponseDtoV2> getProductByIdV2(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductByIdV2(id));
    }

    @PutMapping("/v1/sale-items/{id}")
    public ResponseEntity<ProductItemResponseDTO> updateProduct(@PathVariable Integer id,
                                                                @Valid @RequestBody ProductItemRequestDTO productItemRequestDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productItemRequestDTO));
    }

    @PutMapping("/v2/sale-items/{id}")
    public ResponseEntity<ProductItemRequestDtoV2> updateProductV2(
            @PathVariable Integer id,
            @Valid @ModelAttribute ProductItemRequestDtoV2 product,
            @RequestParam(required = false) List<MultipartFile> newImages) {
        return ResponseEntity.ok(productService.updateProductV2(id, product, product.getRemoveFileNames(), product.getOrderImages(), newImages));
    }

    @PutMapping("/v2/sale-items/{id}/images")
    public ResponseEntity<ProductItemRequestDtoV2> updateProductImages(
            @PathVariable Integer id,
            @Valid @ModelAttribute ProductRequestImageDTO productImageInfo,
            @RequestParam(required = false) List<MultipartFile> newImages) {
        return ResponseEntity.ok(productService.updateProductV2(id, productImageInfo.getRemoveFileNames(), productImageInfo.getOrderImages(), newImages));
    }


    @DeleteMapping("/v1/sale-items/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Integer id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v2/sale-items/{id}")
    public ResponseEntity<Void> deleteProductByIdV2(@PathVariable Integer id) {
        productService.deleteProductByIdV2(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v1/sale-items")
    public ResponseEntity<ProductItemResponseDTO> createProduct(@Valid @RequestBody ProductItemRequestDTO product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product));
    }

    @PostMapping("/v2/sale-items")
    public ResponseEntity<ProductItemResponseDtoV2> createProduct(
            @Valid @ModelAttribute ProductItemRequestDtoV2 product,
            @RequestParam(required = false) List<MultipartFile> images) {

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product, images));
    }

    @GetMapping("/sale-items/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = fileService.loadFileAsResource(fileName);

        String contentType;
        try {
            Path filePath = resource.getFile().toPath();
            contentType = Files.probeContentType(filePath); // ตรวจ MIME type
        } catch (Exception e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"") // inline = แสดงใน browser
                .body(resource);
    }

}
