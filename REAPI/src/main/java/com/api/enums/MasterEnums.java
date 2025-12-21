package com.api.enums;

import lombok.Getter;

public final class MasterEnums {

	@Getter
	public enum InquiryType {
		LEGAL("Regular Package", "Package"), BANK_LOAN("Premium Package", "Package"),
		BUY_HOME("Deluxe Package", "Package"), BUY_COMMERCIAL("Deluxe Package", "Package"),
		RENT_HOME("Deluxe Package", "Package"), RENT_COMMERCIAL("Deluxe Package", "Package");

		private final String description;
		private final String category;

		InquiryType(String description, String category) {
			this.description = description;
			this.category = category;
		}

	}

	@Getter

	public enum DocumentStatus {
		PENDING("PENDING", "DocumentStatus"), VERIFIED("VERIFIED", "DocumentStatus"),
		REOPEN("REOPEN", "DocumentStatus"), CANCELLED("CANCELLED", "DocumentStatus"),
		DELETED("DELETED", "DocumentStatus"), REJECTED("REJECTED", "DocumentStatus");

		private final String description;
		private final String category;

		DocumentStatus(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	@Getter
	public enum InquiryStatus {
		NEW("Regular Package", "Package"), CLOSED("Premium Package", "Package"),
		INPROGRESS("Deluxe Package", "Package");

		private final String description;
		private final String category;

		InquiryStatus(String description, String category) {
			this.description = description;
			this.category = category;
		}

	}

	private MasterEnums() {
	}

	// ------------------- PACKAGE TYPE -------------------
	@Getter

	public enum PackageEnum {
		REGULAR("Regular Package", "Package"), PREMIUM("Premium Package", "Package"), ELITE("Elite Package", "Package"),
		DELUX("Deluxe Package", "Package");

		private final String description;
		private final String category;

		PackageEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	// ------------------- USER STATUS -------------------
	@Getter

	public enum UserStatusEnum {
		ACTIVE("Active Client", "User Status"), CANCELLED("Cancelled Membership", "User Status"),
		TERMINATED("Terminated Membership", "User Status"), PENDING("Pending Approval Membership", "User Status");

		private final String description;
		private final String category;

		UserStatusEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	// ------------------- PROPERTY STATUS -------------------
	@Getter

	public enum PropertyStatusEnum {
		ACTIVE("Active Property", "Property Status"), CANCELLED("Cancelled Property", "Property Status"),
		TERMINATED("Terminated Property", "Property Status"), PENDING("Pending Approval Property", "Property Status");

		private final String description;
		private final String category;

		PropertyStatusEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	// ------------------- PROPERTY TYPE -------------------
	@Getter

	public enum PropertyTypeEnum {
		PLOT("Plot", "Residential"), APARTMENT("Apartment", "Residential"), HOUSE("House", "Residential"),
		BUILDER_FLOOR("Builder Floor", "Residential"), CONDO("Condo", "Residential"),

		OFFICE("Office", "Commercial"), SHOP("Shop", "Commercial"), SHOWROOM("Showroom", "Commercial"),
		PLOT_SHOP("Shop Plot", "Commercial"),

		AGRICULTURAL("Agricultural Land", "Agricultural");

		private final String description;
		private final String category;

		PropertyTypeEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	// ------------------- USER ROLES -------------------
	@Getter

	public enum UserRoleEnum {
		ADMIN("Administrator", "User Role"), AGENT("Agent", "User Role"), CLIENT("Client", "User Role");

		private final String description;
		private final String category;

		UserRoleEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	// ------------------- LAUNCH TYPE -------------------
	@Getter

	public enum LaunchTypeEnum {
		READY_TO_MOVE("Ready to Move", "Launch Type"), PRE_LAUNCH("Pre Launch", "Launch Type"),
		RESALE("Resale", "Launch Type"), UNDER_CONSTRUCTION("Under Construction", "Launch Type");

		private final String description;
		private final String category;

		LaunchTypeEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}

	}

	// ------------------- RELATION TYPE -------------------
	@Getter

	public enum RelationTypeEnum {
		AGENT("Agent Relationship", "User Relation"), CLIENT("Client Relationship", "User Relation"),
		PROSPECT_CLIENT("Prospective Client", "User Relation"), EMPLOYEE("Employee Relationship", "User Relation");

		private final String description;
		private final String category;

		RelationTypeEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	// ------------------- USER RELATION STATUS -------------------
	@Getter

	public enum UserRelationStatusEnum {
		ACTIVE("Active Client", "Client Status"), CLOSED("Deal Closed", "Client Status"),
		REJECTED("Rejected Client", "Client Status");

		private final String description;
		private final String category;

		UserRelationStatusEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	// ------------------- USER INQUIRY STATUS -------------------
	@Getter

	public enum UserInquiryStatusEnum {
		ACTIVE("Active Inquiry", "Inquiry Status"), CLOSED("Closed Inquiry", "Inquiry Status"),
		REJECTED("Rejected Inquiry", "Inquiry Status");

		private final String description;
		private final String category;

		UserInquiryStatusEnum(String description, String category) {
			this.description = description;
			this.category = category;
		}
	}

	// ------------------- DOCUMENT LEGAL SERVICE PROVIDER STATUS
	// -------------------
	@Getter

	public enum DocumentLegalServiceProviderStatus {
		ACTIVE("Active Vendor", "Vendor Status"), CANCELLED("Cancelled Vendor", "Vendor Status"),
		TERMINATED("Terminated Vendor", "Vendor Status"), PENDING("Pending Approval Vendor", "Vendor Status");

		private final String description;
		private final String category;

		DocumentLegalServiceProviderStatus(String description, String category) {
			this.description = description;
			this.category = category;
		}

	}
}
