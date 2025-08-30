package ssi.backend.controllers;

import com.nimbusds.jose.JOSEException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ssi.backend.dtos.LoginRequestDTO;
import ssi.backend.dtos.UserResponseDTO;
import ssi.backend.entities.Buyer;
import ssi.backend.entities.Seller;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.exceptions.UnauthorizedException;
import ssi.backend.repositories.BuyerRepository;
import ssi.backend.repositories.SellerRepository;
import ssi.backend.services.JwtService;
import ssi.backend.services.UserService;

import java.text.ParseException;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/v2/user/verify-token")
    public ResponseEntity<String> verifyToken(@RequestParam String jwtToken) throws ParseException, JOSEException {
        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is missing.");
        }
        jwtService.validateVerifyEmailToken(jwtToken);

        return ResponseEntity.ok("Successfully verified");
    }

    @PostMapping("/v2/user/verify-email")
    public ResponseEntity<UserResponseDTO> verifyEmail(@RequestParam String jwtToken)
            throws ParseException, JOSEException {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new BadRequestException("Token is missing.");
        }

        jwtService.validateVerifyEmailToken(jwtToken);

        UserResponseDTO user = userService.verifyEmail(jwtToken);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/v2/users/authentications")
    public ResponseEntity<Object> authenticateUser(@RequestBody LoginRequestDTO dto) {
        String email = dto.getEmail();
        String rawPassword = dto.getPassword();

        Buyer buyer = buyerRepository.findByEmail(email);
        if (buyer != null) {
            if (!"ACTIVE".equalsIgnoreCase(buyer.getStatus())) {
                throw new BadRequestException("Account is not active");
            }
            if (passwordEncoder.matches(rawPassword, buyer.getPassword())) {
                return ResponseEntity.ok().body("Login Successfully");
            } else {
                throw new UnauthorizedException("Email or Password is incorrect");
            }
        }

        Seller seller = sellerRepository.findByEmail(email);
        if (seller != null) {
            if (!"ACTIVE".equalsIgnoreCase(seller.getStatus())) {
                throw new BadRequestException("Account is not active");
            }
            if (passwordEncoder.matches(rawPassword, seller.getPassword())) {
                return ResponseEntity.ok().body("Login Successfully");
            } else {
                throw new UnauthorizedException("Email or Password is incorrect");
            }
        }

        throw new UnauthorizedException("Email or Password is incorrect");
    }

}
