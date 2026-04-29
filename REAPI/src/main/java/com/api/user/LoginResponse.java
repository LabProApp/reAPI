package com.api.user;

/**
 * Response payload returned by the login endpoint upon successful authentication.
 *
 * <p>Carries a JWT bearer token and a {@link UserDto} containing the authenticated
 * user's profile data. The token must be included in subsequent requests via the
 * {@code Authorization: Bearer <token>} header.
 */
public class LoginResponse {

	private String token;
	private UserDto user;

	/**
	 * Constructs a {@code LoginResponse} with the supplied JWT and user profile.
	 *
	 * @param token the JWT bearer token generated for the authenticated session
	 * @param user  the DTO representing the authenticated user's profile
	 */
	public LoginResponse(String token, UserDto user) {
		this.token = token;
		this.user = user;
	}

	/** Returns the JWT bearer token for the authenticated session. */
	public String getToken() {
		return token;
	}

	/** Returns the DTO containing the authenticated user's profile information. */
	public UserDto getUser() {
		return user;
	}
}
