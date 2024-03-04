package com.hrsupportcentresq014.controllers;

import com.hrsupportcentresq014.dtos.request.AuthenticationRequest;
import com.hrsupportcentresq014.dtos.response.AuthenticationResponse;
import com.hrsupportcentresq014.services.TokenService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The AuthenticationController class manages user authentication within the HR Support Centre system.
 * It handles user login requests and generates authentication tokens for valid users.
 *
 * Key Features:
 * - Provides an endpoint for user login, generating authentication tokens upon successful authentication.
 * - Utilizes Spring Security for handling authentication logic and generating tokens.
 *
 * If I were to solve this problem again:
 * - I would ensure proper validation and sanitization of user input to prevent security vulnerabilities such as SQL injection or XSS attacks.
 * - I might consider implementing additional security measures such as token expiration or refresh mechanisms to enhance security.
 * - I would explore integrating logging frameworks like Logback or Log4j for better monitoring and troubleshooting capabilities.
 *
 */



@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthenticationController {
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@NonNull @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(tokenService.authenticateUser(request));
    }
}
