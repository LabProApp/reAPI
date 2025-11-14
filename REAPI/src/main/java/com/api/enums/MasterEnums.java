package com.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MasterEnums {

	@Getter
	@RequiredArgsConstructor
	public enum PackageEnum implements baseEnum {
		REGULAR("Regular", "Package"), PREMIUM("Premium", "Package"), ELITE("Elite", "Package"),

		DELUX("Delux", "Package");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum UserStatusEnum implements baseEnum {
		ACTIVE("Active Client", "User Status"), CANCELLED("Cancelled Membership", "User Status"),
		TERMINATED("Terminated Membership", "User Status"),

		PENDING("Pending Approval Membership", "User Status");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum PropertyStatusEnum implements baseEnum {
		ACTIVE("Active Property", "Property Status"), CANCELLED("Cancelled Property", "Property Status"),
		TERMINATED("Terminated Property", "Property Status"),

		PENDING("Pending Approval Property", "Property Status");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum PropertyTypeEnum implements baseEnum {
		PLOT("Plot", "Residential"), APARTMENT("Apartment", "Residential"), HOUSE("House", "Residential"),
		BUILDER_FLOOR("Builder Floor", "Residential"), CONDO("Condo", "Residential"),

		OFFICE("Office", "Commercial"), SHOP("Shop", "Commercial"), SHOWROOM("Showroom", "Commercial"),
		PLOT_SHOP("Shop Plot", "Commercial"),

		AGRICULTURAL("Agricultural Land", "Agricultural");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum UserRoleEnum implements baseEnum {
		ADMIN("Administrator role", "User Role"), USER("Agent", "User Role"), CLIENT("Client", "User Role"),
		AGENT("Agent", "User Role");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum LaunchTypeEnum implements baseEnum {
		READY_TO_MOVE("Ready to Move", "Property Status"), PRE_LAUNCH("Pre Launch", "Property Status"),
		RESALE("Resale", "Property Status"), UNDER_CONSTRUCTION("Under Construction", "Property Status");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum RelationTypeEnum implements baseEnum {
		AGENT("Agent Relationship", "User Relation"), CLIENT("Client Relationship", "User Relation"),
		PROSPECT_CLIENT("Prospective Client", "User Relation"), EMPLOYEE("Employee Relationship", "User Relation");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum UserRelationStatusEnum implements baseEnum {
		ACTIVE("Active Client", "Client Status"), CLOSED("Deal Closed", "Client Status"),
		REJECTED("Rejected Client", "Client Status");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum UserInquiryStatusEnum implements baseEnum {
		ACTIVE("Active Client", "Client Status"), CLOSED("Deal Closed", "Client Status"),
		REJECTED("Rejected Client", "Client Status");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum DocumentLegalServiceProviderStatus implements baseEnum {
		ACTIVE("Active Client", "Vendor Status"), CANCELLED("Cancelled Membership", "Vendor Status"),
		TERMINATED("Terminated Membership", "Vendor Status"),

		PENDING("Pending Approval Membership", "Vendor Status");

		private final String description;
		private final String category;

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCategory() {
			return category;
		}
	}
}
