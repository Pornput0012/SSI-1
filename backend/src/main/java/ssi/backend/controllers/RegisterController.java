package ssi.backend.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import ssi.backend.dtos.RegisterRequestDTO;
import ssi.backend.dtos.UserResponseDTO;
import ssi.backend.entities.User;
import ssi.backend.services.UserService;

@RestController
@RequestMapping("/itb-mshop/v2/auth")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class RegisterController {

    @Autowired
    private UserService registerService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/buyers")
    public ResponseEntity<List<User>> getAllBuyers() {
        return ResponseEntity.ok(registerService.getAllBuyers());
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<User>> getAllSellers() {
        return ResponseEntity.ok(registerService.getAllSellers());
    }

//    @GetMapping("/buyers/email")
//    public ResponseEntity<List<String>> getBuyersByEmail() {
//        return ResponseEntity.ok(registerService.getAllUserEmails());
//    }
//
//    @GetMapping("/sellers/email")
//    public ResponseEntity<List<String>> getSellersByEmail() {
//        return ResponseEntity.ok(registerService.getAllSellerEmails());
//    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @ModelAttribute RegisterRequestDTO user,
                                                        @RequestParam(required = false) List<MultipartFile> images) {
        if ("BUYER".equalsIgnoreCase(user.getUserType()) || "SELLER".equalsIgnoreCase(user.getUserType())) {

            return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(registerService.createUser(user,images), UserResponseDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid accountType. Must be BUYER or SELLER");
        }
    }
}