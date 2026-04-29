package com.api.userrelation;

import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;
import com.api.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing a directional relationship between two {@link User} records.
 *
 * <p>Models associations such as CLIENT_OF, AGENT_OF, and BROKER_OF by mapping a
 * primary {@code user} (e.g., a Dealer or Agent) to a {@code relatedUser} (e.g., a
 * Client or Owner) with an explicit {@code relationType} enum. Both user references
 * use LAZY fetching and are annotated with {@link JsonBackReference} to prevent
 * circular serialisation. An optional {@code comments} field allows notes to be
 * attached to the relationship, and a {@code status} field tracks whether the
 * relation is currently active.</p>
 *
 * <p>Mapped to the {@code user_relations} database table; extends {@link BaseEntity}
 * for audit fields.</p>
 */
@Entity
@Table(name = "user_relations")
public class UserRelation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// The main user (e.g., Dealer, Agent)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference(value = "user-main-relations")
	private User user;

	// The related user (e.g., Client, Owner)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "related_user_id", nullable = false)
	@JsonBackReference(value = "user-related-relations")
	private User relatedUser;

	// The type of relationship (Client, Owner, etc.)
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MasterEnums.RelationTypeEnum relationType;

	// Optional comments or notes about the relationship
	@Column(length = 500)
	private String comments;

	// Status of the relationship
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MasterEnums.UserRelationStatusEnum status = MasterEnums.UserRelationStatusEnum.ACTIVE;

	// --- Getters and Setters ---

	/**
	 * Returns the unique identifier of this user relation record.
	 *
	 * @return the relation ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of this user relation record.
	 *
	 * @param id the relation ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the primary user in this relation (e.g., the Dealer or Agent).
	 *
	 * @return the primary {@link User}
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the primary user in this relation (e.g., the Dealer or Agent).
	 *
	 * @param user the primary {@link User} to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the related user in this relation (e.g., the Client or Owner).
	 *
	 * @return the related {@link User}
	 */
	public User getRelatedUser() {
		return relatedUser;
	}

	/**
	 * Sets the related user in this relation (e.g., the Client or Owner).
	 *
	 * @param relatedUser the related {@link User} to set
	 */
	public void setRelatedUser(User relatedUser) {
		this.relatedUser = relatedUser;
	}

	/**
	 * Returns the type of relationship between the two users
	 * (e.g., CLIENT_OF, AGENT_OF, BROKER_OF).
	 *
	 * @return the relation type enum value
	 */
	public MasterEnums.RelationTypeEnum getRelationType() {
		return relationType;
	}

	/**
	 * Sets the type of relationship between the two users.
	 *
	 * @param relationType the relation type enum value to set
	 */
	public void setRelationType(MasterEnums.RelationTypeEnum relationType) {
		this.relationType = relationType;
	}

	/**
	 * Returns any optional comments or notes attached to this relation.
	 *
	 * @return the comments string, or {@code null} if none
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Sets optional comments or notes for this relation.
	 *
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Returns the current status of this relation (e.g., ACTIVE or INACTIVE).
	 *
	 * @return the relation status enum value
	 */
	public MasterEnums.UserRelationStatusEnum getStatus() {
		return status;
	}

	/**
	 * Sets the current status of this relation.
	 *
	 * @param status the relation status enum value to set
	 */
	public void setStatus(MasterEnums.UserRelationStatusEnum status) {
		this.status = status;
	}
}
