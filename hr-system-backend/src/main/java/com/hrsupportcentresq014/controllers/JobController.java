package com.hrsupportcentresq014.controllers;

import com.hrsupportcentresq014.dtos.response.JobSearchResponse;
import com.hrsupportcentresq014.exceptions.NoJobsFoundException;
import com.hrsupportcentresq014.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/*
 /**
 * The JobController class manages job-related operations within the HR Support Centre system.
 * It facilitates the filtering of job postings based on various criteria such as keywords, department, and sorting.
 *
 * Key Features:
 * - Provides an endpoint for filtering job postings based on specified criteria like keywords, department, and sorting options.
 * - Supports pagination to handle large datasets efficiently.
 * - Implements error handling for cases where no jobs are found based on the filtering criteria.
 *
 * If I were to solve this problem again:
 * - I would enhance the search functionality to support more advanced filtering options and improve search accuracy.
 * - I would optimize database queries for better performance, especially when dealing with complex search queries.
 * - I would consider implementing caching mechanisms to reduce the load on the database for frequently accessed job data.
 * - I would explore integrating logging frameworks like Logback or Log4j for better monitoring and troubleshooting capabilities.
 */

 */


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job")
public class JobController {
    private JobService jobService;
    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<JobSearchResponse>> filterJob(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "6") Integer size,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "filter", defaultValue = "newest") String filter,
            @RequestParam(value = "department", required = false) String department) throws NoJobsFoundException {
        Pageable pageable = PageRequest.of(page, size);
         return ResponseEntity.ok(jobService.filterJobs(keywords, filter, department, pageable));
    }
}
