package com.api.projects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Project} entities.
 *
 * <p>Extends both {@link JpaRepository} for standard CRUD operations and
 * {@link JpaSpecificationExecutor} for dynamic query composition via JPA
 * {@link Specification} predicates, enabling flexible filtered searches
 * used by {@link ProjectService}.</p>
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    /**
     * Returns a page of projects matching the given specification.
     *
     * @param spec     the JPA {@link Specification} predicate (may be composed from multiple criteria)
     * @param pageable pagination and sorting descriptor
     * @return a {@link Page} of matching {@link Project} entities
     */
    Page<Project> findAll(Specification<Project> spec, Pageable pageable);
}
