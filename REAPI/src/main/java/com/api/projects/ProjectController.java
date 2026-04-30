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
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for real estate project endpoints.
 *
 * <p>Exposes read-only APIs under {@code /api/projects} for listing projects
 * with optional filters (city, builder, status, property type, price range)
 * and for fetching a single project by its ID. Pagination and sorting are
 * fully configurable via query parameters.</p>
 */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Real Estate Projects", description = "APIs for listing real estate projects")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService service;

    /**
     * Lists projects with optional filters, pagination, and sorting.
     *
     * <p>All filter parameters are optional; omitting them returns all projects.
     * The {@code sort} parameter accepts a field name and optional direction
     * separated by a comma (e.g., {@code projectName,asc}).</p>
     *
     * @param city         optional city filter
     * @param builder      optional builder name filter
     * @param status       optional project status filter
     * @param propertyType optional property type filter
     * @param minPrice     optional minimum price filter
     * @param maxPrice     optional maximum price filter
     * @param page         zero-based page index (default {@code 0})
     * @param size         page size (default {@code 20})
     * @param sort         sort field and direction, e.g., {@code projectName,asc} (default)
     * @return a {@link ResponseEntity} containing a page of {@link ProjectDto} objects
     */
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

    /**
     * Fetches a single project by its primary key.
     *
     * @param id the project ID
     * @return {@code 200 OK} with the {@link ProjectDto} if found,
     *         or {@code 404 Not Found} if no project exists with the given ID
     */
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
