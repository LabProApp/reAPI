package com.api.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration.hours:24}")
	private long expirationHours;

	private SecretKey signingKey;

	@PostConstruct
	void init() {
		byte[] keyBytes;
		String input = (secret == null || secret.isBlank()) ? "default-jwt-secret-change-me" : secret;
		try {
			keyBytes = Decoders.BASE64.decode(input);
		} catch (IllegalArgumentException e) {
			// Not valid Base64 — treat as raw text
			keyBytes = input.getBytes(StandardCharsets.UTF_8);
		}
		if (keyBytes.length < 32) {
			// Stretch short keys via SHA-256 so init never crashes.
			log.warn("JwtUtil - jwt.secret decodes to {} bytes; deriving a 32-byte key via SHA-256. "
					+ "Set a stronger JWT_SECRET in production.", keyBytes.length);
			try {
				keyBytes = MessageDigest.getInstance("SHA-256").digest(keyBytes);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("SHA-256 unavailable", e);
			}
		}
		this.signingKey = Keys.hmacShaKeyFor(keyBytes);
		log.info("JwtUtil - initialized; token TTL = {}h", expirationHours);
	}

	public String generateToken(Long userId, String identifier, String role) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.subject(String.valueOf(userId))
				.claim("identifier", identifier)
				.claim("role", role)
				.issuedAt(new Date(now))
				.expiration(new Date(now + expirationHours * 3600_000L))
				.signWith(signingKey)
				.compact();
	}

	public Claims extractClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public Long extractUserId(String token) {
		return Long.parseLong(extractClaims(token).getSubject());
	}

	public boolean isTokenValid(String token) {
		try {
			Claims claims = extractClaims(token);
			return claims.getExpiration() == null || claims.getExpiration().after(new Date());
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}
