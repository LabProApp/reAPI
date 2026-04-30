package com.api.security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	
	public String generateToken(Long userId, String identifier, String role) {
		return Jwts.builder()
				.subject(String.valueOf(userId))
				.claim("identifier", identifier)
				.claim("role", role)
				.signWith(signingKey())
				.compact();
	}

	
	public Claims extractClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	
	public Long extractUserId(String token) {
		return Long.parseLong(extractClaims(token).getSubject());
	}

	
	public boolean isTokenValid(String token) {
		try {
			extractClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	
	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}
}
