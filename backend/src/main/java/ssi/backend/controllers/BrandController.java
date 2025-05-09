package ssi.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssi.backend.dtos.BrandDTO;
import ssi.backend.services.BrandService;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop/v1/brands")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("")
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }
}
