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

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-test-email")
    public ResponseEntity<String> sendEmail() {
        emailService.sendEmail("moomost123@gmail.com", "Email Testing", "This is test email");
        return ResponseEntity.ok("Email Sent");
    }
}
