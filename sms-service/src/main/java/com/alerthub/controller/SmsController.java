package com.alerthub.controller;

import com.alerthub.dto.SmsMessage;
import com.alerthub.service.SmsSenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@Tag(name = "SMS Sender", description = "Endpoints for sending SMS")
public class SmsController {

    @Autowired
    private SmsSenderService smsSenderService;

    @PostMapping("/send")
    @Operation(summary = "Send an SMS manually")
    public ResponseEntity<String> sendSms(@Valid @RequestBody SmsMessage smsMessage) {
        // קריאה ל-Service. אם תהיה שגיאה, ה-GlobalExceptionHandler יטפל בה אוטומטית!
        smsSenderService.sendSms(smsMessage);

        // אם הגענו לכאן, סימן שהכל עבר בהצלחה (סטטוס 200)
        return ResponseEntity.ok("SMS request processed successfully");
    }
}