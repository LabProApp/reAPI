package com.api.enums;

import lombok.Getter;

/**
 * Central container for all domain enumerations used throughout the real estate API.
 *
 * <p>This class is {@code final} and cannot be instantiated; its sole purpose is to
 * namespace related enum types. Every nested enum implements {@link BaseEnum} transitively
 * through the enclosing class and is annotated with Lombok {@code @Getter} to expose
 * any additional fields defined on constants.
 *
 * <p>Nested enums cover the following domains:
 * <ul>
 *   <li>Employment and loan types for bank/finance integrations</li>
 *   <li>Inquiry types and statuses for customer service flows</li>
 *   <li>Document lifecycle states</li>
 *   <li>Lead pipeline stages</li>
 *   <li>User roles, account statuses, and subscription packages</li>
 *   <li>Property types, statuses, and launch types</li>
 *   <li>User relation types and statuses</li>
 * </ul>
 */
public final class MasterEnums implements BaseEnum {

	/**
	 * Employment classification used in loan eligibility assessments.
	 */
	@Getter
	public enum EmploymentType {
		/** The applicant receives a fixed salary from an employer. */
		SALARIED,
		/** The applicant runs their own business or freelance practice. */
		SELF_EMPLOYED,
		/** The applicant is employed by a government or public-sector organisation. */
		PUBLIC_SECTOR;

	}

	/**
	 * Categories of loan products offered through the platform.
	 */
	@Getter
	public enum LoanType {
		/** Standard home purchase loan. */
		HOME,
		/** Loan Against Property — secured against an existing asset. */
		LAP,
		/** Transfer of an existing loan to a new lender for better terms. */
		BALANCE_TRANSFER

	}

	/**
	 * Types of service inquiry a user can raise on the platform.
	 */
	@Getter
	public enum InquiryType {
		/** Inquiry for a home loan. */
		HOME_LOAN,
		/** Inquiry about property registration services. */
		PROPERTY_REGISTRATION,
		/** General inquiry about a listed property. */
		PROPERTY_INQUIRY,
		/** Inquiry about drafting or renewing a rent agreement. */
	    RENT_AGREEMENT,
	    /** Loan Against Property inquiry. */
	    LAP,
	    /** Balance transfer of an existing loan. */
	    BALANCE_TRANSFER,
	    /** Intent to purchase a residential property. */
	    BUY_HOME,
	    /** Intent to sell a residential property. */
	    SELL_HOME,
	    /** Inquiry for document-related legal services. */
	    DOCUMENT_SERVICES,
	    /** Transfer of an existing loan to a different lender. */
	    LOAN_TRANSFER,
	    /** Inquiry about renting a residential property. */
	    HOME_RENTAL,
	    /** Inquiry related to commercial real estate. */
	    COMMERCIAL;

	}

	/**
	 * Lifecycle states for documents submitted to the platform.
	 */
	@Getter
	public enum DocumentStatus {
		/** Document has been uploaded but not yet reviewed. */
		NOT_VERIFIED,
		/** Document is awaiting review by an agent or admin. */
		PENDING,
		/** Document has been successfully verified. */
		VERIFIED,
		/** Document verification was reopened for further review. */
		REOPEN,
		/** Document processing was cancelled. */
		CANCELLED,
		/** Document has been removed from the system. */
		DELETED,
		/** Document failed verification and was rejected. */
		REJECTED;

	}

	/**
	 * High-level status values for a user-raised service inquiry.
	 */
	@Getter
	public enum InquiryStatus {
		/** Inquiry has just been created. */
		NEW,
		/** Inquiry has been resolved and closed. */
		CLOSED,
		/** Inquiry is actively being worked on. */
		INPROGRESS;
	}

	/**
	 * Pipeline stages for a property sales or rental lead.
	 */
	@Getter
	public enum LeadStatus {
		/** Lead has been created but not yet actioned. */
		NEW,
		/** Initial contact has been made with the lead. */
		CONTACTED,
		/** A property visit has been scheduled. */
		VISIT_PLANNED,
		/** The property visit has taken place. */
		VISIT_DONE,
		/** Price or terms negotiation is in progress. */
		NEGOTIATING,
		/** Deal has been successfully closed. */
		CLOSED_WON,
		/** Deal was not closed; lead has been lost. */
		CLOSED_LOST,
		/** Lead has been abandoned without a definitive outcome. */
		DROPPED;
	}

	private MasterEnums() {
	}

	// ------------------- PACKAGE TYPE -------------------

	/**
	 * Subscription package tiers available to users of the platform.
	 */
	@Getter
	public enum PackageEnum {
		/** Basic tier with standard features. */
		REGULAR,
		/** Enhanced tier with additional features and visibility. */
		PREMIUM,
		/** Top tier with all platform features unlocked. */
		ELITE,
		/** Luxury tier for high-end real estate professionals. */
		DELUX;

	}

	// ------------------- USER STATUS -------------------

	/**
	 * Account lifecycle states for a {@link com.api.user.User}.
	 */
	@Getter
	public enum UserStatusEnum {
		/** Account is active and the user can log in. */
		ACTIVE,
		/** Account has been cancelled by the user or admin. */
		CANCELLED,
		/** Account has been permanently terminated. */
		TERMINATED,
		/** Account is awaiting OTP verification or admin approval. */
		PENDING;

	}

	// ------------------- PROPERTY STATUS -------------------

	/**
	 * Listing lifecycle states for a property.
	 */
	@Getter
	public enum PropertyStatusEnum {
		/** Property listing is live and visible to users. */
		ACTIVE,
		/** Property listing has been cancelled by the owner. */
		CANCELLED,
		/** Property listing has been permanently removed. */
		TERMINATED,
		/** Property listing is awaiting approval before going live. */
		PENDING;

	}

	// ------------------- PROPERTY TYPE -------------------

	/**
	 * Physical categories of real estate properties listed on the platform.
	 */
	@Getter
	public enum PropertyTypeEnum {
		/** Open land plot. */
		PLOT,
		/** Multi-unit residential apartment. */
		APARTMENT,
		/** Independent residential house. */
		HOUSE,
		/** Builder floor / independent floor unit. */
		BUILDER_FLOOR,
		/** Paying Guest accommodation. */
		PG,
		/** Commercial office space. */
		OFFICE,
		/** Retail shop space. */
		SHOP,
		/** Showroom or display space. */
		SHOWROOM,
		/** Combined plot and shop property. */
		PLOT_SHOP,
		/** Shared co-working or flexible workspace. */
		CO_WORKING,
		/** Agricultural land. */
		AGRICULTURAL;

	}

	// ------------------- USER ROLES -------------------

	/**
	 * Access-control roles assigned to users of the platform.
	 */
	@Getter
	public enum UserRoleEnum {
		/** Platform administrator with full system access. */
		ADMIN,
		/** Real estate agent managing properties and leads. */
		AGENT,
		/** End-user client browsing or inquiring about properties. */
		CLIENT;

	}

	// ------------------- LAUNCH TYPE -------------------

	/**
	 * Indicates the development or availability stage of a property.
	 */
	@Getter
	public enum LaunchTypeEnum {
		/** Property is built and available for immediate possession. */
		READY_TO_MOVE,
		/** Newly launched property project. */
		NEW_LAUNCH,
		/** Previously owned property being re-listed. */
		RESALE,
		/** Property is still being built and not yet ready for possession. */
		UNDER_CONSTRUCTION;

	}

	// ------------------- RELATION TYPE -------------------

	/**
	 * Describes the nature of the relationship between two users on the platform.
	 */
	@Getter
	public enum RelationTypeEnum {
		/** The related user acts as an agent for the primary user. */
		AGENT,
		/** The related user is a client of the primary user. */
		CLIENT,
		/** The related user is a prospective (not yet active) client. */
		PROSPECT_CLIENT,
		/** The related user is an employee of the primary user. */
		EMPLOYEE;

	}

	// ------------------- USER RELATION STATUS -------------------

	/**
	 * Lifecycle states for a user-to-user relationship record.
	 */
	@Getter
	public enum UserRelationStatusEnum {
		/** The relationship is currently active. */
		ACTIVE,
		/** The relationship has ended and been closed. */
		CLOSED,
		/** A relationship invitation or request was rejected. */
		REJECTED;

	}

	// ------------------- USER INQUIRY STATUS -------------------

	/**
	 * Lifecycle states for a user-level inquiry record.
	 */
	@Getter
	public enum UserInquiryStatusEnum {
		/** Inquiry is open and active. */
		ACTIVE,
		/** Inquiry has been resolved and closed. */
		CLOSED,
		/** Inquiry was rejected by the handling agent or admin. */
		REJECTED;

	}

	// ------------------- DOCUMENT LEGAL SERVICE PROVIDER STATUS -------------------

	/**
	 * Operational states for an external document or legal service provider.
	 */
	@Getter
	public enum DocumentLegalServiceProviderStatus {
		/** Provider is active and available on the platform. */
		ACTIVE,
		/** Provider's engagement has been cancelled. */
		CANCELLED,
		/** Provider's account has been permanently terminated. */
		TERMINATED,
		/** Provider is registered but awaiting activation. */
		PENDING;

	}
}
