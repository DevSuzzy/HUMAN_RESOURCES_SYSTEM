package com.hrsupportcentresq014.services.serviceImpl;

import com.hrsupportcentresq014.dtos.response.JobResponseDto;
import com.hrsupportcentresq014.dtos.response.JobSearchResponse;
import com.hrsupportcentresq014.entities.Job;
import com.hrsupportcentresq014.exceptions.NoJobsFoundException;
import com.hrsupportcentresq014.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The JobServiceImpl class implements the JobService interface and provides functionalities
 * for filtering and retrieving job listings in the HR Support Centre system.
 *
 * Key Features:
 * - Filters job listings based on keywords, department, and date filters.
 * - Retrieves job listings according to specified sorting criteria (newest, oldest, past week).
 * - Handles MongoDB queries to fetch and process job data efficiently.
 *
 * If I were to solve this problem again:
 * - I would enhance the error handling mechanism to provide more informative error messages.
 * - I would optimize database queries and improve performance, especially for large datasets.
 * - I would explore ways to modularize the code further to enhance readability and maintainability.
 * - I would consider implementing caching mechanisms to improve response times for frequently accessed data.
 *
 */

@Service
public class JobServiceImpl implements JobService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public JobServiceImpl( MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public Page<JobSearchResponse> filterJobs(String keywords, String filter, String department, Pageable pageable) throws NoJobsFoundException {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        if (keywords != null) {
            criteriaList.add(Criteria.where("title").regex(keywords, "i")); // Using regex for case-insensitive search
        }

        if (department != null) {
            criteriaList.add(Criteria.where("departmentName").is(department));
        }

        if ("newest".equalsIgnoreCase(filter)) {
            // Sort by descending order of createdOn field to get the newest jobs
            query.with(Sort.by(Sort.Direction.DESC, "createdOn"));
        } else if ("oldest".equalsIgnoreCase(filter)) {
            // Sort by ascending order of createdOn field to get the oldest jobs
            query.with(Sort.by(Sort.Direction.ASC, "createdOn"));
        }

        if ("past_week".equalsIgnoreCase(filter)) {
            // Filter jobs created in the past week
            LocalDate weekAgo = LocalDate.now().minusWeeks(1);
            criteriaList.add(Criteria.where("createdOn").gte(weekAgo.atStartOfDay()));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        Page<Job> jobPage = PageableExecutionUtils.getPage(mongoTemplate.find(query, Job.class), pageable, () -> mongoTemplate.count(query, Job.class));

        if (jobPage.isEmpty()) {
            throw new NoJobsFoundException("No Jobs found");
        }

        List<JobResponseDto> jobResponseDtoList = jobPage.getContent().stream()
                .filter(Job::getIsActive)
                .map(this::toJobResponseDTO).collect(Collectors.toList());
        JobSearchResponse jobSearchResponse = new JobSearchResponse(jobResponseDtoList, jobPage.getTotalPages(), jobPage.getTotalElements(), null);
        return new PageImpl<>(Collections.singletonList(jobSearchResponse), pageable, 1);
    }

    @Override
    public Page<JobSearchResponse> filterAllJobs(String keywords, String filter, String department, Pageable pageable) throws NoJobsFoundException {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        if (keywords != null) {
            criteriaList.add(Criteria.where("title").regex(keywords, "i")); // Using regex for case-insensitive search
        }

        if (department != null) {
            criteriaList.add(Criteria.where("departmentName").is(department));
        }

        if ("newest".equalsIgnoreCase(filter)) {
            // Sort by descending order of createdOn field to get the newest jobs
            query.with(Sort.by(Sort.Direction.DESC, "createdOn"));
        } else if ("oldest".equalsIgnoreCase(filter)) {
            // Sort by ascending order of createdOn field to get the oldest jobs
            query.with(Sort.by(Sort.Direction.ASC, "createdOn"));
        }

        if ("past_week".equalsIgnoreCase(filter)) {
            // Filter jobs created in the past week
            LocalDate weekAgo = LocalDate.now().minusWeeks(1);
            criteriaList.add(Criteria.where("createdOn").gte(weekAgo.atStartOfDay()));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        Page<Job> jobPage = PageableExecutionUtils.getPage(mongoTemplate.find(query, Job.class), pageable, () -> mongoTemplate.count(query, Job.class));

        if (jobPage.isEmpty()) {
            throw new NoJobsFoundException("No Jobs found");
        }

        List<JobResponseDto> jobResponseDtoList = jobPage.getContent().stream()
                .map(this::toJobResponseDTO).collect(Collectors.toList());
        JobSearchResponse jobSearchResponse = new JobSearchResponse(jobResponseDtoList, jobPage.getTotalPages(), jobPage.getTotalElements(), null);
        return new PageImpl<>(Collections.singletonList(jobSearchResponse), pageable, 1);
    }


    private JobResponseDto toJobResponseDTO(Job job) {
        return JobResponseDto.builder()
                .title(job.getTitle())
                .description(job.getDescription())
                .departmentName(job.getDepartmentName())
                .closingDate(job.getClosingDate())
                .createdOn(job.getCreatedOn().toString())
                .isActive(job.getIsActive())
                .build();
    }
}
