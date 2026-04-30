package com.api.enums;

import lombok.Getter;

public final class MasterEnums implements BaseEnum {

	
	@Getter
	public enum EmploymentType {
		
		SALARIED,
		
		SELF_EMPLOYED,
		
		PUBLIC_SECTOR;

	}

	
	@Getter
	public enum LoanType {
		
		HOME,
		
		LAP,
		
		BALANCE_TRANSFER

	}

	
	@Getter
	public enum InquiryType {
		
		HOME_LOAN,
		
		PROPERTY_REGISTRATION,
		
		PROPERTY_INQUIRY,
		
	    RENT_AGREEMENT,
	    
	    LAP,
	    
	    BALANCE_TRANSFER,
	    
	    BUY_HOME,
	    
	    SELL_HOME,
	    
	    DOCUMENT_SERVICES,
	    
	    LOAN_TRANSFER,
	    
	    HOME_RENTAL,
	    
	    COMMERCIAL;

	}

	
	@Getter
	public enum DocumentStatus {
		
		NOT_VERIFIED,
		
		PENDING,
		
		VERIFIED,
		
		REOPEN,
		
		CANCELLED,
		
		DELETED,
		
		REJECTED;

	}

	
	@Getter
	public enum InquiryStatus {
		
		NEW,
		
		CLOSED,
		
		INPROGRESS;
	}

	
	@Getter
	public enum LeadStatus {
		
		NEW,
		
		CONTACTED,
		
		VISIT_PLANNED,
		
		VISIT_DONE,
		
		NEGOTIATING,
		
		CLOSED_WON,
		
		CLOSED_LOST,
		
		DROPPED;
	}

	private MasterEnums() {
	}

	// ------------------- PACKAGE TYPE -------------------

	
	@Getter
	public enum PackageEnum {
		
		REGULAR,
		
		PREMIUM,
		
		ELITE,
		
		DELUX;

	}

	// ------------------- USER STATUS -------------------

	
	@Getter
	public enum UserStatusEnum {
		
		ACTIVE,
		
		CANCELLED,
		
		TERMINATED,
		
		PENDING;

	}

	// ------------------- PROPERTY STATUS -------------------

	
	@Getter
	public enum PropertyStatusEnum {
		
		ACTIVE,
		
		CANCELLED,
		
		TERMINATED,
		
		PENDING;

	}

	// ------------------- PROPERTY TYPE -------------------

	
	@Getter
	public enum PropertyTypeEnum {
		
		PLOT,
		
		APARTMENT,
		
		HOUSE,
		
		BUILDER_FLOOR,
		
		PG,
		
		OFFICE,
		
		SHOP,
		
		SHOWROOM,
		
		PLOT_SHOP,
		
		CO_WORKING,
		
		AGRICULTURAL;

	}

	// ------------------- USER ROLES -------------------

	
	@Getter
	public enum UserRoleEnum {
		
		ADMIN,
		
		AGENT,
		
		CLIENT;

	}

	// ------------------- LAUNCH TYPE -------------------

	
	@Getter
	public enum LaunchTypeEnum {
		
		READY_TO_MOVE,
		
		NEW_LAUNCH,
		
		RESALE,
		
		UNDER_CONSTRUCTION;

	}

	// ------------------- RELATION TYPE -------------------

	
	@Getter
	public enum RelationTypeEnum {
		
		AGENT,
		
		CLIENT,
		
		PROSPECT_CLIENT,
		
		EMPLOYEE;

	}

	// ------------------- USER RELATION STATUS -------------------

	
	@Getter
	public enum UserRelationStatusEnum {
		
		ACTIVE,
		
		CLOSED,
		
		REJECTED;

	}

	// ------------------- USER INQUIRY STATUS -------------------

	
	@Getter
	public enum UserInquiryStatusEnum {
		
		ACTIVE,
		
		CLOSED,
		
		REJECTED;

	}

	// ------------------- DOCUMENT LEGAL SERVICE PROVIDER STATUS -------------------

	
	@Getter
	public enum DocumentLegalServiceProviderStatus {
		
		ACTIVE,
		
		CANCELLED,
		
		TERMINATED,
		
		PENDING;

	}
}
