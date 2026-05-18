package com.api.admin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Single payload for the admin dashboard's "at a glance" cards. Counts only —
 * no PII. Numbers are computed eagerly per request; if the user table ever
 * grows large enough that these scans matter, switch to denormalised
 * counters or a materialised view.
 */
public class AdminStatsDto {

	/** Total registered users (across all plans). */
	private long totalUsers;

	/** Total property listings (across SALE + RENT). */
	private long totalProperties;

	/** SALE listings only. */
	private long propertiesForSale;

	/** RENT listings only. */
	private long propertiesForRent;

	/** Plan-change requests waiting for admin review. */
	private long pendingPlanRequests;

	/** Users on DELUX or PREMIUM (i.e. paying customers). */
	private long activeSubscriptions;

	/** Per-plan headcount, keyed by {@code MasterEnums.PackageEnum#name()}. */
	private Map<String, Long> usersByPlan = new LinkedHashMap<>();

	public long getTotalUsers() { return totalUsers; }
	public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

	public long getTotalProperties() { return totalProperties; }
	public void setTotalProperties(long totalProperties) { this.totalProperties = totalProperties; }

	public long getPropertiesForSale() { return propertiesForSale; }
	public void setPropertiesForSale(long propertiesForSale) { this.propertiesForSale = propertiesForSale; }

	public long getPropertiesForRent() { return propertiesForRent; }
	public void setPropertiesForRent(long propertiesForRent) { this.propertiesForRent = propertiesForRent; }

	public long getPendingPlanRequests() { return pendingPlanRequests; }
	public void setPendingPlanRequests(long pendingPlanRequests) { this.pendingPlanRequests = pendingPlanRequests; }

	public long getActiveSubscriptions() { return activeSubscriptions; }
	public void setActiveSubscriptions(long activeSubscriptions) { this.activeSubscriptions = activeSubscriptions; }

	public Map<String, Long> getUsersByPlan() { return usersByPlan; }
	public void setUsersByPlan(Map<String, Long> usersByPlan) { this.usersByPlan = usersByPlan; }
}
