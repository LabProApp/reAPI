package com.api.projects;

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

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Real Estate Projects", description = "APIs for listing real estate projects")
public class ProjectController {

    @Autowired
    private ProjectService service;

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

        // 🛠 FIX: Safe parsing of sort parameters
        String sortField = sort[0];
        Sort.Direction direction = Sort.Direction.ASC;

        if (sort.length > 1) {
            direction = Sort.Direction.fromString(sort[1]);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<ProjectDto> result =
                service.listProjects(city, builder, status, propertyType, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
