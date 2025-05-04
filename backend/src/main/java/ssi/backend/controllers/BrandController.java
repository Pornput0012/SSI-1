package ssi.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi.backend.dtos.ProductItemDTO;
import ssi.backend.entities.Brand;
import ssi.backend.services.BrandService;
import ssi.backend.services.ProductService;

@RestController
@RequestMapping("/itb-mshop/v1/brands")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class BrandController {

    @Autowired
    private BrandService brandService;


    @DeleteMapping("/{id}")
    public ResponseEntity<Brand> deleteProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(brandService.deleteBrand(id));
    }

    @PostMapping
    public ResponseEntity<Brand> createProductById(@RequestBody Brand brand) {
        return ResponseEntity.ok(brandService.createBrand(brand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateProductById(@PathVariable Integer id,@RequestBody Brand brand) {
        return ResponseEntity.ok(brandService.updateBrand(id,brand));
    }
}
