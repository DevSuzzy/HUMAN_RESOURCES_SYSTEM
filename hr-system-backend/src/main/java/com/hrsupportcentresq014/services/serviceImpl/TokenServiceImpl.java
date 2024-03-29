package com.hrsupportcentresq014.services.serviceImpl;

import com.hrsupportcentresq014.dtos.request.AuthenticationRequest;
import com.hrsupportcentresq014.dtos.response.AuthenticationResponse;
import com.hrsupportcentresq014.entities.Employee;
import com.hrsupportcentresq014.entities.Token;
import com.hrsupportcentresq014.exceptions.EmailNotFoundException;
import com.hrsupportcentresq014.repositories.EmployeeRepository;
import com.hrsupportcentresq014.repositories.TokenRepository;
import com.hrsupportcentresq014.security_config.utils.JwtUtils;
import com.hrsupportcentresq014.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * The TokenServiceImpl class implements the TokenService interface to handle user authentication and token management in the HR Support Centre system.
 *
 * Key Features:
 * - Authenticates users based on their email and password credentials.
 * - Generates JWT tokens upon successful authentication.
 * - Saves generated tokens and updates user login status.
 * - Revokes existing tokens when a user logs in, preventing multiple active sessions.
 *
 * If I were to solve this problem again:
 * - I would consider implementing additional security measures such as rate limiting to prevent brute force attacks.
 * - I would optimize the token management logic for better performance, especially in scenarios with a large number of concurrent users.
 * - I would enhance error handling to provide more informative error messages for different authentication scenarios.
 * - I would explore integrating with third-party authentication providers for enhanced security and flexibility.
 *
 */




@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private  final EmployeeRepository employeeRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    @Override
    public AuthenticationResponse authenticateUser(AuthenticationRequest request) {
        var user = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(()->
                        new EmailNotFoundException("User with email: " +request.getEmail() +" not found"));

        user.setLoggedIn(true);
        Employee employee = employeeRepository.save(user);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        revokeToken(user);
        var jwtToken = jwtUtils.generateToken(user);
        saveToken(user,jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .issuedAt(jwtUtils.getIssuedAt(jwtToken))
                .expiredAt(jwtUtils.getExpiration(jwtToken))
                .employee(user)
                .build();
    }

    private void saveToken(Employee user, String jwtToken) {
        var token = Token.builder()
                .jwtToken(jwtToken)
                .expired(false)
                .revoked(false)
                .employee(user)
                .build();
        tokenRepository.save(token);
    }

    private void revokeToken(Employee user){
        var validTokenByUser= tokenRepository.findTokenByEmployeeAndExpiredIsFalseAndRevokedIsFalse(user);
        if(validTokenByUser.isEmpty())
            return;
        validTokenByUser.forEach(t->{
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validTokenByUser);
    }
}
