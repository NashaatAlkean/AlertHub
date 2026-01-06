package com.alerthub.controller;
import com.alerthub.dto.EmailRequest;
import com.alerthub.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@Tag(name = "Email Sender", description = "Endpoints for sending email notifications") // חלק מדרישת Swagger
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "Send an email manually", description = "Triggers the email service logic for testing")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
        // קריאה ל-Service שבנינו קודם
        emailService.sendEmail(emailRequest);

        // החזרת תשובה ללקוח (Postman)
        return ResponseEntity.ok("Email request processed for: " + emailRequest.getTo());
    }
}