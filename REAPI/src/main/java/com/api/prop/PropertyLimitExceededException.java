package com.api.prop;

/**
 * Thrown when a user tries to post a property beyond their plan's allowance.
 *
 * <p>Mapped to <strong>HTTP 402 Payment Required</strong> by the global
 * exception handler so the mobile client can distinguish "needs upgrade"
 * from generic validation errors and prompt accordingly.</p>
 */
public class PropertyLimitExceededException extends RuntimeException {

	private final String planName;
	private final int limit;
	private final int currentCount;

	public PropertyLimitExceededException(String message, String planName,
			int limit, int currentCount) {
		super(message);
		this.planName = planName;
		this.limit = limit;
		this.currentCount = currentCount;
	}

	public String getPlanName() { return planName; }
	public int getLimit() { return limit; }
	public int getCurrentCount() { return currentCount; }
}
