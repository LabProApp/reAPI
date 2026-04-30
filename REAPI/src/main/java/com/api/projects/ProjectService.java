package com.api.projects;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Service layer for real estate project operations.
 *
 * <p>Provides methods to list projects with dynamic filtering via JPA
 * {@link Specification} predicates built by {@link ProjectSpecifications},
 * and to fetch individual projects by their primary key. Results are
 * mapped to {@link ProjectDto} using ModelMapper before being returned
 * to the caller.</p>
 */
@Service
public class ProjectService {

	private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private ProjectRepository repository;

	@Autowired
	private ModelMapper mapper;

	/**
	 * Returns a paginated list of projects that match the supplied optional filters.
	 *
	 * <p>Any {@code null} filter parameter is silently ignored, allowing callers to
	 * provide only the criteria they care about. Pagination and sorting are
	 * controlled by the {@code pageable} argument.</p>
	 *
	 * @param city         filter by exact city name, or {@code null} to skip
	 * @param builder      filter by exact builder name, or {@code null} to skip
	 * @param status       filter by project status (e.g., "Completed"), or {@code null} to skip
	 * @param propertyType filter by property type (e.g., "Residential"), or {@code null} to skip
	 * @param minPrice     filter projects whose minimum price is at least this value, or {@code null} to skip
	 * @param maxPrice     filter projects whose maximum price is at most this value, or {@code null} to skip
	 * @param pageable     pagination and sorting descriptor
	 * @return a page of {@link ProjectDto} objects matching the criteria
	 */
	public Page<ProjectDto> listProjects(String city, String builder, String status, String propertyType,
			Double minPrice, Double maxPrice, Pageable pageable) {
		log.info("listProjects - Listing projects [city={}, builder={}, status={}, propertyType={}, price={}-{}]",
				city, builder, status, propertyType, minPrice, maxPrice);

		Specification<Project> spec = Specification.where(ProjectSpecifications.hasCity(city))
				.and(ProjectSpecifications.hasBuilder(builder)).and(ProjectSpecifications.hasStatus(status))
				.and(ProjectSpecifications.hasPropertyType(propertyType))
				.and(ProjectSpecifications.hasMinPrice(minPrice)).and(ProjectSpecifications.hasMaxPrice(maxPrice));

		Page<Project> page = repository.findAll(spec, pageable);
		log.info("listProjects - Found {} projects (total={})", page.getNumberOfElements(), page.getTotalElements());

		return page.map(project -> mapper.map(project, ProjectDto.class));
	}

	/**
	 * Fetches a single project by its primary key.
	 *
	 * @param id the project ID to look up
	 * @return an {@link Optional} containing the matching {@link ProjectDto},
	 *         or {@link Optional#empty()} if no project exists with the given ID
	 */
	public Optional<ProjectDto> getById(Long id) {
		log.info("getById - Fetching project id={}", id);
		Optional<ProjectDto> result = repository.findById(id).map(entity -> mapper.map(entity, ProjectDto.class));
		if (result.isPresent()) {
			log.info("getById - Project id={} found: {}", id, result.get().getProjectName());
		} else {
			log.warn("getById - Project id={} not found", id);
		}
		return result;
	}
}
