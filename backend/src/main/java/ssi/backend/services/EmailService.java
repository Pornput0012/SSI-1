package ssi.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Async
    public void sendEmail(String to, String subject, String body) {
        log.info("[Local Email Service] (No external SMTP required) To: {}, Subject: {}\nBody:\n{}", to, subject, body);
    }
}
