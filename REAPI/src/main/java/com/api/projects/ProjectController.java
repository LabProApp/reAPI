package com.api.projects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Real Estate Projects", description = "APIs for listing real estate projects")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService service;

    @Operation(summary = "List real estate projects with filters and pagination")
    @GetMapping
    public ResponseEntity<Page<ProjectDto>> listProjects(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String builder,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "projectName,asc") String[] sort) {

        log.info("GET /api/projects - Listing projects [city={}, builder={}, status={}, page={}, size={}]",
                city, builder, status, page, size);

        String sortField = sort[0];
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort.length > 1) {
            direction = Sort.Direction.fromString(sort[1]);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<ProjectDto> result = service.listProjects(city, builder, status, propertyType, minPrice, maxPrice, pageable);

        log.info("GET /api/projects - Returned {} projects (page {}/{})",
                result.getNumberOfElements(), page, result.getTotalPages());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get project by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long id) {
        log.info("GET /api/projects/{} - Fetching project", id);
        return service.getById(id)
                .map(dto -> {
                    log.info("GET /api/projects/{} - Project fetched: {}", id, dto.getProjectName());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("GET /api/projects/{} - Project not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
