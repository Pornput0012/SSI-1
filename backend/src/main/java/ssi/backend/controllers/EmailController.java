package ssi.backend.controllers;

import com.nimbusds.jose.JOSEException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssi.backend.services.EmailService;
import ssi.backend.services.JwtService;

import java.text.ParseException;

@RestController
@RequestMapping("/itb-mshop")
public class EmailController {
    private final EmailService emailService;
    private final JwtService jwtService;

    public EmailController(EmailService emailService, JwtService jwtService) {
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    @GetMapping("/send-test-email")
    public ResponseEntity<String> sendEmail() {
        emailService.sendEmail("moomost123@gmail.com", "Email Testing", "This is test email");
        return ResponseEntity.ok("Email Sent");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) throws ParseException, JOSEException {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is missing.");
        }

        jwtService.validateToken(token);

        return ResponseEntity.ok("Email verified successfully");
    }
}
