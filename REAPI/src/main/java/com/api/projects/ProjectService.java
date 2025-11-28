package com.api.projects;


import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository repository;

	@Autowired
	private ModelMapper mapper;

	public Page<ProjectDto> listProjects(String city, String builder, String status, String propertyType,
			Double minPrice, Double maxPrice, Pageable pageable) {

		Specification<Project> spec = Specification.where(ProjectSpecifications.hasCity(city))
				.and(ProjectSpecifications.hasBuilder(builder)).and(ProjectSpecifications.hasStatus(status))
				.and(ProjectSpecifications.hasPropertyType(propertyType))
				.and(ProjectSpecifications.hasMinPrice(minPrice)).and(ProjectSpecifications.hasMaxPrice(maxPrice));

		Page<Project> page = repository.findAll(spec, pageable);

		return page.map(project -> mapper.map(project, ProjectDto.class));
	}

	public Optional<ProjectDto> getById(Long id) {
		return repository.findById(id).map(entity -> mapper.map(entity, ProjectDto.class));
	}
}
