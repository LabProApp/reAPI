package com.api.commons;

/**
 * Custom exception to represent 404 (Not Found) errors. Used when an entity
 * (like Property or User) doesn't exist in DB.
 */
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code ResourceNotFoundException} with the specified detail message.
	 *
	 * @param message human-readable description of the missing resource
	 *                (e.g., {@code "Property not found with code: ABC123"})
	 */
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
