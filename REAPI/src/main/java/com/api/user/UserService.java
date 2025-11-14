package com.api.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.documents.DocumentRepository;
import com.api.documents.Documents;
import com.api.enums.MasterEnums;
import com.api.prop.Property;
import com.api.prop.PropertyRepository;
import com.api.userproperty.UserPropertyRelation;
import com.api.userproperty.UserPropertyRelationRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PropertyRepository propertyRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private UserPropertyRelationRepository propertyRelationRepository;

	// ---------------- REGISTER ----------------
	public ResponseEntity<String> signup(User user) {
		if (user.getEmail() == null && user.getMobile() == null) {
			return ResponseEntity.badRequest().body("Email or mobile required");
		}

		if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
			return ResponseEntity.badRequest().body("Email already in use");
		}

		if (user.getMobile() != null && userRepository.existsByMobile(user.getMobile())) {
			return ResponseEntity.badRequest().body("Mobile already in use");
		}

		if (user.getUserRole() == null) {
			user.setUserRole(MasterEnums.UserRoleEnum.CLIENT);
		}
		userRepository.save(user);
		return ResponseEntity.ok("User registered successfully!");
	}

	// ---------------- LOGIN ----------------
	public ResponseEntity<String> login(User req) {
		try {
			User user = null;

			if (req.getEmail() != null && req.getEmail().contains("@")) {
				user = userRepository.findByEmail(req.getEmail())
						.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
			} else if (req.getMobile() != null) {
				user = userRepository.findByMobile(req.getMobile())
						.orElseThrow(() -> new IllegalArgumentException("Invalid mobile or password"));
			} else {
				return ResponseEntity.badRequest().body("Email or mobile required for login");
			}

			if (!req.getPassword().equalsIgnoreCase(user.getPassword())) {
				return ResponseEntity.status(401).body("Invalid credentials");
			}

			return ResponseEntity.ok("Login successful for user: " + user.getName());

		} catch (Exception ex) {
			return ResponseEntity.status(500).body("Login failed: " + ex.getMessage());
		}
	}

	// ---------------- LOGOUT ----------------
	// Since there's no JWT, logout is just client-side
	public ResponseEntity<String> logout() {
		return ResponseEntity.ok("Logged out successfully!");
	}

	// ---------------- PROFILE ----------------
	public Optional<User> getProfile(String emailOrMobile) {
		if (emailOrMobile.contains("@"))
			return userRepository.findByEmail(emailOrMobile);
		else
			return userRepository.findByMobile(emailOrMobile);
	}

	public ResponseEntity<String> updateProfile(User updatedUser) {
		Optional<User> userOpt = updatedUser.getEmail() != null ? userRepository.findByEmail(updatedUser.getEmail())
				: userRepository.findByMobile(updatedUser.getMobile());

		if (userOpt.isPresent()) {
			User existing = userOpt.get();

			if (updatedUser.getName() != null)
				existing.setName(updatedUser.getName());
			if (updatedUser.getAddress() != null)
				existing.setAddress(updatedUser.getAddress());
			if (updatedUser.getMobile() != null)
				existing.setMobile(updatedUser.getMobile());

			userRepository.save(existing);
			return ResponseEntity.ok("Profile updated successfully!");
		}
		return ResponseEntity.badRequest().body("User not found!");
	}

	// ---------------- RESET PASSWORD ----------------
	public ResponseEntity<String> resetPassword(String emailOrMobile, String newPassword) {
		Optional<User> userOpt = emailOrMobile.contains("@") ? userRepository.findByEmail(emailOrMobile)
				: userRepository.findByMobile(emailOrMobile);

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.setPassword(newPassword);
			userRepository.save(user);
			return ResponseEntity.ok("Password reset successfully!");
		}
		return ResponseEntity.badRequest().body("User not found!");
	}

	// ---------------- PROPERTY RELATIONS ----------------
	public UserPropertyRelation markFavourite(Long userId, Long propertyId) {

		User user = userRepository.findById(userId).orElseThrow();
		Property property = propertyRepository.findById(propertyId).orElseThrow();

		UserPropertyRelation relation = propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
				.orElseGet(() -> {
					UserPropertyRelation newRelation = new UserPropertyRelation();
					newRelation.setUser(user);
					newRelation.setProperty(property);
					return newRelation;
				});

		/*
		 * UserPropertyRelation relation =
		 * propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
		 * .orElseThrow(() -> new ResourceNotFoundException(
		 * "Relation not found for userId: " + userId + " and propertyId: " +
		 * propertyId));
		 */

		if (relation.isFavourite()) {
			relation.setFavourite(false);
			relation.setFavouriteDate(null);
		} else {
			relation.setFavourite(true);
			relation.setFavouriteDate(LocalDateTime.now());
		}

		return propertyRelationRepository.save(relation);

	}

	public UserPropertyRelation markInterested(Long userId, Long propertyId) {

		User user = userRepository.findById(userId).orElseThrow();
		Property property = propertyRepository.findById(propertyId).orElseThrow();

		UserPropertyRelation relation = propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
				.orElseGet(() -> {
					UserPropertyRelation newRelation = new UserPropertyRelation();
					newRelation.setUser(user);
					newRelation.setProperty(property);
					return newRelation;
				});

		/*
		 * UserPropertyRelation relation =
		 * propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
		 * .orElseThrow(() -> new ResourceNotFoundException(
		 * "Relation not found for userId: " + userId + " and propertyId: " +
		 * propertyId));
		 */

		if (relation.isInterested()) {
			relation.setInterested(false);
			relation.setInterestDate(null);
		} else {
			relation.setInterested(true);
			relation.setInterestDate(LocalDateTime.now());
		}

		return propertyRelationRepository.save(relation);

	}

	/*
	 * private UserPropertyRelation updateRelation(Long userId, Long propertyId,
	 * Boolean favFlag, Boolean interestFlag) { User user =
	 * userRepository.findById(userId).orElseThrow(); Property property =
	 * propertyRepository.findById(propertyId).orElseThrow();
	 * 
	 * UserPropertyRelation relation =
	 * propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
	 * .orElseGet(() -> { UserPropertyRelation newRelation = new
	 * UserPropertyRelation(); newRelation.setUser(user);
	 * newRelation.setProperty(property); return newRelation; });
	 * 
	 * 
	 * UserPropertyRelation relation =
	 * propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
	 * .orElseThrow(() -> new ResourceNotFoundException(
	 * "Relation not found for userId: " + userId + " and propertyId: " +
	 * propertyId));
	 * 
	 * 
	 * if (favFlag) { relation.setFavourite(false); relation.setFavouriteDate(null);
	 * } else { relation.setFavourite(true);
	 * relation.setFavouriteDate(LocalDateTime.now()); }
	 * 
	 * if (interestFlag != null) { relation.setInterested(interestFlag);
	 * relation.setInterestDate(interestFlag ? LocalDateTime.now() : null); }
	 * 
	 * 
	 * return propertyRelationRepository.save(relation);
	 * 
	 * }
	 */

	public List<UserPropertyRelation> getFavourites(Long userId) {
		return propertyRelationRepository.findByUserId(userId).stream().filter(UserPropertyRelation::isFavourite)
				.toList();
	}

	public List<UserPropertyRelation> getInterests(Long userId) {
		return propertyRelationRepository.findByUserId(userId).stream().filter(UserPropertyRelation::isInterested)
				.toList();
	}

	public ResponseEntity<User> uploadDocuments(Long userId, List<MultipartFile> files, List<String> captions) {

		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String caption = captions.size() > i ? captions.get(i) : null;

			// Upload to S3 or local storage
			String url = "test"; // s3Service.uploadFile(file);

			Documents doc = Documents.builder().docUrl(url).docType(file.getContentType()).caption(caption).user(user)
					.docCategory("USER").build();

			user.getDocuments().add(doc);
		}
		return ResponseEntity.ok(userRepository.save(user));
	}

	public List<Documents> getUserDocuments(Long userId) {
		return documentRepository.findByUserId(userId);
	}

}
