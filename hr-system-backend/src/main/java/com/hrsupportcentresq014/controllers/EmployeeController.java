package com.hrsupportcentresq014.controllers;

import com.hrsupportcentresq014.dtos.request.ChangePasswordRequest;
import com.hrsupportcentresq014.dtos.request.EmployeeProfileRequest;
import com.hrsupportcentresq014.dtos.response.CreateHrResponseDTO;
import com.hrsupportcentresq014.dtos.response.EmployeeProfileResponse;
import com.hrsupportcentresq014.dtos.response.EmployeeViewProfileResponse;
import com.hrsupportcentresq014.exceptions.UserAlreadyExistsException;
import com.hrsupportcentresq014.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * The EmployeeController class manages employee-related functionalities within the HR Support Centre system.
 * It handles requests related to employee profile management, image and resume uploads, and password changes.
 *
 * Key Features:
 * - Supports updating employee profiles, including personal information and profile picture.
 * - Allows employees to upload their resumes and view their profiles.
 * - Facilitates password changes for authenticated users.
 *
 * If I were to solve this problem again:
 * - I would enhance security measures, such as implementing CSRF protection and ensuring secure password storage practices.
 * - I would optimize file upload mechanisms to handle large files efficiently and prevent resource exhaustion.
 * - I would consider implementing rate limiting or CAPTCHA verification for sensitive operations to prevent abuse.
    * - I would explore integrating additional authentication mechanisms such as multi-factor authentication for enhanced security.
    */
 */


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/staff")
@Log4j2
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PutMapping("/profile")
    public ResponseEntity<EmployeeProfileRequest> updateEmployeeProfile(@RequestBody @Valid EmployeeProfileRequest employeeProfileRequest) {
        log.info("Updating Employee Profile");
        EmployeeProfileRequest response = employeeService.updateEmployeeProfile(employeeProfileRequest);
        log.info("Your Profile has been successfully updated");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/add-pic")
    public ResponseEntity<String> uploadProfilePic(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        String imageURL = employeeService.uploadImage(multipartFile);
        return ResponseEntity.ok(imageURL);
    }

    @PostMapping("/add-resume")
        public ResponseEntity<String> uploadResume(@RequestParam("resume") MultipartFile multipartFile){
        String resumeUrl = employeeService.uploadResume(multipartFile);
        return ResponseEntity.ok(resumeUrl);
        }

    @GetMapping("/viewProfile")
    public ResponseEntity<EmployeeViewProfileResponse> viewProfile(){
        EmployeeViewProfileResponse employeeViewProfileResponse = employeeService.viewProfile();
        return ResponseEntity.ok(employeeViewProfileResponse);
    }
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest changerPassword, Authentication auth) {
        return new ResponseEntity<>(employeeService.changePassword(changerPassword, auth), HttpStatus.OK);
    }

}
