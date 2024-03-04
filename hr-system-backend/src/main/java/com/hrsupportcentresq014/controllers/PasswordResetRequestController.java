package com.hrsupportcentresq014.controllers;

import com.hrsupportcentresq014.dtos.request.PasswordResetRequest;
import com.hrsupportcentresq014.services.PasswordResetRequestService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The PasswordResetRequestController class manages password reset functionalities within the HR Support Centre system.
 * It handles requests for resetting forgotten passwords and completing password reset actions.
 *
 * Key Features:
 * - Provides endpoints for initiating password reset requests and completing password reset actions.
 * - Utilizes email notifications to facilitate the password reset process.
 *
 * If I were to solve this problem again:
 * - I would enhance security measures, such as implementing email verification for password reset requests to prevent misuse.
 * - I would improve error handling to provide clear feedback to users during the password reset process.
 * - I would explore integrating templating engines for email notifications to provide a better user experience.
 * - I would consider implementing rate limiting to prevent abuse of the password reset functionality.
 */


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/password")
public class PasswordResetRequestController {
    private final PasswordResetRequestService resetRequestService;

    public PasswordResetRequestController(PasswordResetRequestService resetRequestService) {
        this.resetRequestService = resetRequestService;
    }

    @PostMapping("/forgot-password/")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest email) throws MessagingException {
        return resetRequestService.resetPassword(email.getEmail());
    }

    @PostMapping("/password-reset-confirmation")
    public ResponseEntity<String> confirmPasswordReset(@RequestParam("resetToken") String resetToken,
                                                       @RequestBody PasswordResetRequest newPassword){
        return resetRequestService.completePasswordReset(resetToken, newPassword.getPassword());
    }
}
