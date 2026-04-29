package com.api.commons;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

/**
 * Abstract base class for all JPA entities in the Real Estate API.
 *
 * <p>Provides common auditing fields — {@code code}, {@code createdTs},
 * {@code lastUpdatedTs}, {@code createdBy}, and {@code updatedBy} — that are
 * automatically populated by Spring Data JPA's {@link AuditingEntityListener}
 * and the {@link AuditorAwareImpl} bean.</p>
 *
 * <p>A short, unique {@code code} value is generated via
 * {@link ShortIdGenerator#generateRandomId()} during the {@link PrePersist}
 * lifecycle callback if one has not already been assigned.</p>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {


	    @Column(name = "code", updatable = false)
	private String code;

	/**
	 * JPA lifecycle callback invoked before the entity is first persisted.
	 *
	 * <p>Assigns a randomly generated short identifier to {@code code} when
	 * no value has been set explicitly.</p>
	 */
	@PrePersist
	public void prePersist() {
	    if (this.code == null) {
	        this.code =  ShortIdGenerator.generateRandomId(); // call your generator logic
	    }
	}

    @CreatedDate
    @Column(name = "created_ts", updatable = false)
    private LocalDateTime createdTs;

    @LastModifiedDate
    @Column(name = "last_updated_ts")
    private LocalDateTime lastUpdatedTs;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

	/** Returns the short unique code assigned to this entity. */
	public String getCode() {
		return code;
	}

	/** Sets the short unique code for this entity. */
	public void setCode(String code) {
		this.code = code;
	}

	/** Returns the timestamp at which this entity was first persisted. */
	public LocalDateTime getCreatedTs() {
		return createdTs;
	}

	/** Sets the creation timestamp for this entity. */
	public void setCreatedTs(LocalDateTime createdTs) {
		this.createdTs = createdTs;
	}

	/** Returns the timestamp of the most recent update to this entity. */
	public LocalDateTime getLastUpdatedTs() {
		return lastUpdatedTs;
	}

	/** Sets the last-updated timestamp for this entity. */
	public void setLastUpdatedTs(LocalDateTime lastUpdatedTs) {
		this.lastUpdatedTs = lastUpdatedTs;
	}

	/** Returns the identifier of the principal who created this entity. */
	public String getCreatedBy() {
		return createdBy;
	}

	/** Sets the identifier of the principal who created this entity. */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/** Returns the identifier of the principal who last updated this entity. */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/** Sets the identifier of the principal who last updated this entity. */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}
