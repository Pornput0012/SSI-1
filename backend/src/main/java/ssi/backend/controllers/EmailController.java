package ssi.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ssi.backend.services.EmailService;

@RestController
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-test-email")
    public ResponseEntity<String> sendEmail() {
        //mock send email
        emailService.sendEmail("moomost123@gmail.com","Email Testing","This is test email");
        return ResponseEntity.ok("Email Sent");
    }
}
