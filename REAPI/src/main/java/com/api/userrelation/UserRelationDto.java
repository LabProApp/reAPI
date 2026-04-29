package com.api.userrelation;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;

/**
 * Data Transfer Object for creating or updating a {@link UserRelation}.
 *
 * <p>Carries only the IDs of the two involved users ({@code userId} and
 * {@code relatedUserId}) rather than full {@link com.api.user.User} objects,
 * avoiding unnecessary entity loading and preventing circular serialisation
 * issues associated with {@code @JsonBackReference} fields on the entity.
 * The controller is responsible for constructing lightweight {@link com.api.user.User}
 * stubs from these IDs before delegating to the service layer.</p>
 *
 * <p>Extends {@link BaseDto} for common audit/metadata fields.</p>
 */
public class UserRelationDto extends BaseDto {

	private Long id;

	private Long userId;

	private Long relatedUserId;

	private MasterEnums.RelationTypeEnum relationType;

	private String comments;

	private MasterEnums.UserRelationStatusEnum status = MasterEnums.UserRelationStatusEnum.ACTIVE;

	// --- Getters and Setters ---

	/**
	 * Returns the unique identifier of the relation record (used in responses).
	 *
	 * @return the relation ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the relation record.
	 *
	 * @param id the relation ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the ID of the primary user in the relation (e.g., the Dealer or Agent).
	 *
	 * @return the primary user ID
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * Sets the ID of the primary user in the relation.
	 *
	 * @param userId the primary user ID to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * Returns the ID of the related user in the relation (e.g., the Client or Owner).
	 *
	 * @return the related user ID
	 */
	public Long getRelatedUserId() {
		return relatedUserId;
	}

	/**
	 * Sets the ID of the related user in the relation.
	 *
	 * @param relatedUserId the related user ID to set
	 */
	public void setRelatedUserId(Long relatedUserId) {
		this.relatedUserId = relatedUserId;
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
	 * Returns the current status of the relation (e.g., ACTIVE or INACTIVE).
	 *
	 * @return the relation status enum value
	 */
	public MasterEnums.UserRelationStatusEnum getStatus() {
		return status;
	}

	/**
	 * Sets the current status of the relation.
	 *
	 * @param status the relation status enum value to set
	 */
	public void setStatus(MasterEnums.UserRelationStatusEnum status) {
		this.status = status;
	}
}
