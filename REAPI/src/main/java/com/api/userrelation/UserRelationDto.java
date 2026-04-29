package com.api.userrelation;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;

public class UserRelationDto extends BaseDto {

	private Long id;

	private Long userId;

	private Long relatedUserId;

	private MasterEnums.RelationTypeEnum relationType;

	private String comments;

	private MasterEnums.UserRelationStatusEnum status = MasterEnums.UserRelationStatusEnum.ACTIVE;

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRelatedUserId() {
		return relatedUserId;
	}

	public void setRelatedUserId(Long relatedUserId) {
		this.relatedUserId = relatedUserId;
	}

	public MasterEnums.RelationTypeEnum getRelationType() {
		return relationType;
	}

	public void setRelationType(MasterEnums.RelationTypeEnum relationType) {
		this.relationType = relationType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public MasterEnums.UserRelationStatusEnum getStatus() {
		return status;
	}

	public void setStatus(MasterEnums.UserRelationStatusEnum status) {
		this.status = status;
	}
}
