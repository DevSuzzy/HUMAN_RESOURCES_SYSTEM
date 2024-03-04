package com.hrsupportcentresq014.controllers;


import com.hrsupportcentresq014.dtos.request.AddPermissionRequest;
import com.hrsupportcentresq014.dtos.request.AddRoleRequest;
import com.hrsupportcentresq014.dtos.request.AdminRequest;
import com.hrsupportcentresq014.dtos.response.AdminResponse;
import com.hrsupportcentresq014.dtos.response.CreateHrResponseDTO;
import com.hrsupportcentresq014.exceptions.UserAlreadyExistsException;
import com.hrsupportcentresq014.services.EmployeeService;
import com.hrsupportcentresq014.services.RoleService;
import com.hrsupportcentresq014.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * This AdminController class manages administrative functionalities within the HR Support Centre system.
 * It handles requests related to user registration, role management, and permission assignment.
 *
 * Key Features:
 * - Allows registration of admin users and HR staff members.
 * - Supports adding roles and assigning permissions to roles.
 * - Utilizes Spring Security annotations for authorization checks.
 *
 * If I were to solve this problem again:
 * - I would maintain the use of Spring Security for role-based access control, as it provides a robust framework for managing permissions.
 * - I would enhance error handling to provide more informative responses for different failure scenarios.
 * - I would explore integrating Swagger for API documentation to improve the developer experience.
 * - I would consider implementing more comprehensive unit and integration tests to ensure the reliability of the endpoints.
 */



@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final EmployeeService employeeService;
    @PostMapping(name = "RegisterAdmin", value = "/register")
    public ResponseEntity<AdminResponse> registerAdmin(@Valid @RequestBody AdminRequest adminRequest){
        log.info("Registering Admin with payload {}", adminRequest);
        AdminResponse adminResponse = userService.register(adminRequest);
        return new ResponseEntity<>(adminResponse, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-roles")
    public ResponseEntity<?> addRoles (@RequestBody AddRoleRequest addRole){
        return new ResponseEntity<>(roleService.addRoles(addRole), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/add-role-permissions/{role_id}")
    public ResponseEntity<?> addPermissions (@RequestBody AddPermissionRequest request, @PathVariable String role_id)
    {
        return new ResponseEntity<>(roleService.addPermission(request, role_id), HttpStatus.CREATED);
    }

    @PostMapping(name =  "RegisterHr", value= "/register-hr" )
    public ResponseEntity<CreateHrResponseDTO> createHr(@RequestBody CreateHrResponseDTO hrDTO) throws UserAlreadyExistsException {
        return new ResponseEntity<>(employeeService.createHr(hrDTO), HttpStatus.CREATED);
    }
}
