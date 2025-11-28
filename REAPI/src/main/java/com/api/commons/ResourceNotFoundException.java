package com.api.commons;

/**
 * Custom exception to represent 404 (Not Found) errors. Used when an entity
 * (like Property or User) doesn't exist in DB.
 */
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
