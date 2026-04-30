package com.api.user;

public class LoginResponse {

	private String token;
	private UserDto user;

	
	public LoginResponse(String token, UserDto user) {
		this.token = token;
		this.user = user;
	}

	
	public String getToken() {
		return token;
	}

	
	public UserDto getUser() {
		return user;
	}
}
