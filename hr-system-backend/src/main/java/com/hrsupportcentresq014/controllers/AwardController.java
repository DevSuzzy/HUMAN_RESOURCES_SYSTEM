package com.hrsupportcentresq014.controllers;

import com.hrsupportcentresq014.dtos.request.AwardRequestDTO;
import com.hrsupportcentresq014.dtos.response.AllAwardsResponseDTO;
import com.hrsupportcentresq014.dtos.response.AwardResponseDTO;
import com.hrsupportcentresq014.services.AwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Collection;



/**
 * The AwardController class manages operations related to awards within the HR Support Centre system.
 * It facilitates the creation of new awards, retrieval of awards by year, and fetching all awards.
 *
 * Key Features:
 * - Supports the creation of new awards by authorized users.
 * - Allows users to retrieve awards by a specific year and fetch all awards paginated.
 * - Implements authorization checks to ensure only authorized users can perform certain actions.
 *
 * If I were to solve this problem again:
 * - I would improve error handling to provide more informative responses in case of exceptions or invalid requests.
 * - I would consider implementing caching mechanisms for frequently accessed award data to improve performance.
 * - I would enhance the documentation of API endpoints for better developer understanding and usability.
 */

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/awards")
public class AwardController {
    private final AwardService awardsService;

    @PostMapping("/register")
    public ResponseEntity<String> createAward(@RequestBody AwardRequestDTO awardRequestDTO) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isHrUser = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_HR"));

        if (!isHrUser) {
            throw new AccessDeniedException("Only HR users are allowed to create awards.");
        }

        String response = awardsService.createAward(awardRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{year}")
    public ResponseEntity<Page<AwardResponseDTO>> getAwardByYear(@PathVariable("year") String year,
                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        var response = awardsService.getAwardByYear(year, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-all-awards")
    public ResponseEntity<AllAwardsResponseDTO> allRewards(@RequestParam(defaultValue = "0") int pageNo
            , @RequestParam(defaultValue="5") int pageSize){

        return ResponseEntity.ok(awardsService.getAllRewards(pageNo, pageSize));
    }


}
