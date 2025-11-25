package com.api.userrelation;

import com.api.BaseEntity;
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
