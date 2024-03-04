package com.hrsupportcentresq014.services.serviceImpl;


import com.hrsupportcentresq014.entities.Employee;
import com.hrsupportcentresq014.entities.PasswordResetRequest;
import com.hrsupportcentresq014.repositories.EmployeeRepository;
import com.hrsupportcentresq014.repositories.PasswordResetRequestRepository;
import com.hrsupportcentresq014.services.MailService;
import com.hrsupportcentresq014.services.PasswordResetRequestService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * The PasswordResetRequestServiceImpl class implements the PasswordResetRequestService interface to handle password reset requests in the HR Support Centre system.
 *
 * Key Features:
 * - Generates a unique reset token and sets an expiration date for password reset requests.
 * - Sends an email to the user with a password reset link containing the reset token.
 * - Completes the password reset process by updating the user's password based on the reset token.
 *
 * If I were to solve this problem again:
 * - I would enhance security measures by implementing token validation mechanisms to prevent misuse of reset tokens.
 * - I would improve error handling for edge cases such as invalid or expired tokens.
 * - I would consider adding logging functionality to track password reset activities for audit purposes.
 * - I would explore additional authentication methods for password reset to enhance user experience and security.
 *

 */


@Service
public class PasswordResetRequestServiceImpl implements PasswordResetRequestService {
    private final PasswordResetRequestRepository resetRequestRepo;
    private final EmployeeRepository employeeRepo;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetRequestServiceImpl(PasswordResetRequestRepository resetRequestRepo, EmployeeRepository employeeRepo, MailService mailService, PasswordEncoder passwordEncoder) {
        this.resetRequestRepo = resetRequestRepo;
        this.employeeRepo = employeeRepo;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<String> resetPassword(String email) throws MessagingException {
        String resetToken;
        resetToken = UUID.randomUUID().toString();
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(10);
        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setEmail(email);
        resetRequest.setResetToken(resetToken);
        resetRequest.setExpirationDate(expirationDate);
        resetRequestRepo.save(resetRequest);
        String resetEmailUrl = "Click the following link to reset your password: http://your_website.com/reset-password?token=" + resetToken;

        mailService.passwordReset(resetEmailUrl, email);
        return ResponseEntity.ok("Please check your email!");
    }

    @Override
    public ResponseEntity<String> completePasswordReset(String resetToken, String newPassword) {
        PasswordResetRequest resetRequest = resetRequestRepo.findPasswordResetRequestByResetToken(resetToken);
        if((resetRequest == null) || resetRequest.getExpirationDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Invalid or expired token");
        }
        else {
            Employee employeeToResetPassword = employeeRepo.findByEmail(resetRequest.getEmail()).orElseThrow(() -> new RuntimeException("Employee not found"));
            employeeToResetPassword.setPassword(passwordEncoder.encode(newPassword));
            employeeRepo.save(employeeToResetPassword);
        }
        return ResponseEntity.ok("Password reset successful");
    }
}