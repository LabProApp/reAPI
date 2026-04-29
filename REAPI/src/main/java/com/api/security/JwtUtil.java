package com.api.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Utility component for creating and validating JSON Web Tokens (JWTs) used
 * throughout the Real Estate API's stateless authentication flow.
 *
 * <p>Tokens are signed with the HS256 algorithm using a Base64-encoded secret
 * key configured via the {@code jwt.secret} application property. The token
 * lifetime is controlled by the {@code jwt.expiration} property (milliseconds).</p>
 *
 * <p>Each token carries the following claims:</p>
 * <ul>
 *   <li><b>subject</b> — the numeric user ID as a string</li>
 *   <li><b>identifier</b> — the user's login identifier (e.g. email or phone)</li>
 *   <li><b>role</b> — the user's application role (e.g. {@code ADMIN}, {@code USER})</li>
 * </ul>
 */
@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long expiration;

	/**
	 * Builds and signs a new JWT for the given user.
	 *
	 * @param userId     the unique numeric identifier of the authenticated user
	 * @param identifier the login identifier (email or phone number) of the user
	 * @param role       the application role assigned to the user
	 * @return a compact, URL-safe JWT string ready to be sent to the client
	 */
	public String generateToken(Long userId, String identifier, String role) {
		return Jwts.builder()
				.subject(String.valueOf(userId))
				.claim("identifier", identifier)
				.claim("role", role)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(signingKey())
				.compact();
	}

	/**
	 * Parses and verifies the supplied JWT, returning its claims payload.
	 *
	 * @param token the compact JWT string to parse
	 * @return the {@link Claims} payload extracted from the verified token
	 * @throws io.jsonwebtoken.JwtException if the token is malformed, expired,
	 *         or the signature cannot be verified
	 */
	public Claims extractClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	/**
	 * Extracts the numeric user ID stored in the JWT subject claim.
	 *
	 * @param token the compact JWT string from which to extract the user ID
	 * @return the {@link Long} user ID encoded in the token's subject
	 */
	public Long extractUserId(String token) {
		return Long.parseLong(extractClaims(token).getSubject());
	}

	/**
	 * Checks whether the supplied JWT is still valid (i.e. not expired and
	 * correctly signed).
	 *
	 * @param token the compact JWT string to validate
	 * @return {@code true} if the token is valid and not yet expired;
	 *         {@code false} if it is expired, malformed, or has an invalid signature
	 */
	public boolean isTokenValid(String token) {
		try {
			return !extractClaims(token).getExpiration().before(new Date());
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Derives the HMAC-SHA256 {@link SecretKey} from the Base64-encoded
	 * {@code jwt.secret} configuration property.
	 *
	 * @return the {@link SecretKey} used to sign and verify JWTs
	 */
	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}
}
