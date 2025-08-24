package ssi.backend.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import ssi.backend.dtos.RegisterRequestDTO;
import ssi.backend.entities.Buyer;
import ssi.backend.entities.Seller;
import ssi.backend.services.RegisterService;

@RestController
@RequestMapping("/itb-mshop/v1/register")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @GetMapping("/buyers")
    public ResponseEntity<List<Buyer>> getAllBuyers() {
        return ResponseEntity.ok(registerService.getAllBuyers());
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<Seller>> getAllSellers() {
        return ResponseEntity.ok(registerService.getAllSellers());
    }

    @GetMapping("/buyers/email")
    public ResponseEntity<List<String>> getBuyersByEmail() {
        return ResponseEntity.ok(registerService.getAllBuyerEmails());
    }

    @GetMapping("/sellers/email")
    public ResponseEntity<List<String>> getSellersByEmail() {
        return ResponseEntity.ok(registerService.getAllSellerEmails());
    }

    @PostMapping("")
    public ResponseEntity<Object> registerUser(@Valid @ModelAttribute RegisterRequestDTO user,
                                          @RequestParam(required = false) List<MultipartFile> images,@RequestParam String userType) {
        if ("BUYER".equalsIgnoreCase(userType)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registerService.createBuyer(user));
        } else if ("SELLER".equalsIgnoreCase(userType)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registerService.createSeller(user,images));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid accountType. Must be BUYER or SELLER");
        }
    }
}