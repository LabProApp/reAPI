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

@Service
public class ProjectService {

	private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private ProjectRepository repository;

	@Autowired
	private ModelMapper mapper;

	
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
