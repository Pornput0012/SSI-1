package ssi.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<Buyer>> getBuyersByEmail(@RequestParam String email) {
        return ResponseEntity.ok(registerService.getAllEmailBuyers(email));
    }

    @GetMapping("/sellers/email")
    public ResponseEntity<List<Seller>> getSellersByEmail(@RequestParam String email) {
        return ResponseEntity.ok(registerService.getAllEmailSellers(email));
    }

    @PostMapping("")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO user) {
        if ("BUYER".equalsIgnoreCase(user.getAccountType())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registerService.createBuyer(user));
        } else if ("SELLER".equalsIgnoreCase(user.getAccountType())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registerService.createSeller(user));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid accountType. Must be BUYER or SELLER");
        }
    }
}