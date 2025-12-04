package com.api.userrelation;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;
import com.api.user.User;

public class UserRelationDto extends BaseDto {

	private Long id;

	private User user;

	private User relatedUser;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getRelatedUser() {
		return relatedUser;
	}

	public void setRelatedUser(User relatedUser) {
		this.relatedUser = relatedUser;
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
