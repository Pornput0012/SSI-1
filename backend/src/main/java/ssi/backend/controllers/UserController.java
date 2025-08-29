package ssi.backend.controllers;

import com.nimbusds.jose.JOSEException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi.backend.dtos.UserResponseDTO;
import ssi.backend.exceptions.BadRequestException;
import ssi.backend.services.JwtService;
import ssi.backend.services.UserService;

import java.text.ParseException;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;

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
    public ResponseEntity<UserResponseDTO> verifyEmail(@RequestParam String jwtToken) throws ParseException, JOSEException {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new BadRequestException("Token is missing.");
        }
        
        jwtService.validateVerifyEmailToken(jwtToken);

        UserResponseDTO user = userService.verifyEmail(jwtToken);

        return ResponseEntity.ok(user);
    }

}
