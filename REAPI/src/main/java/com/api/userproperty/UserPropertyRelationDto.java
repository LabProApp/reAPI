package com.api.userproperty;

import java.time.LocalDateTime;

import com.api.enums.MasterEnums;
import com.api.prop.PropertyDto;
import com.api.user.UserDto;

public class UserPropertyRelationDto {

    private Long id;

    // --- Relations as DTOs ---
    private UserDto user;
    private PropertyDto property;

    // --- Flags ---
    private boolean favourite = false; // renamed from isFavourite
    private boolean inquiry = false;   // renamed from isInquiry

    // --- Dates ---
    private LocalDateTime favouriteDate;
    private LocalDateTime inquiryDate;

    // --- Comments / Notes ---
    private String comments;

    // --- Status ---
    private MasterEnums.UserInquiryStatusEnum status = MasterEnums.UserInquiryStatusEnum.ACTIVE;

    // --- Constructors ---
    public UserPropertyRelationDto() {}

    // --- Logic for flags ---
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
        this.favouriteDate = favourite ? LocalDateTime.now() : null;
    }

    public void setInquiry(boolean inquiry) {
        this.inquiry = inquiry;
        this.inquiryDate = inquiry ? LocalDateTime.now() : null;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public PropertyDto getProperty() {
        return property;
    }

    public void setProperty(PropertyDto property) {
        this.property = property;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public boolean isInquiry() {
        return inquiry;
    }

    public LocalDateTime getFavouriteDate() {
        return favouriteDate;
    }

    public void setFavouriteDate(LocalDateTime favouriteDate) {
        this.favouriteDate = favouriteDate;
    }

    public LocalDateTime getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(LocalDateTime inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public MasterEnums.UserInquiryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(MasterEnums.UserInquiryStatusEnum status) {
        this.status = status;
    }
}
