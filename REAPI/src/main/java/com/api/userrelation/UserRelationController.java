package com.api.userrelation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.user.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-relations")
@Tag(name = "User Relation APIs", description = "Operations related to Maintain Relations between Agents & Clients")
@RequiredArgsConstructor
public class UserRelationController {

	private static final Logger log = LoggerFactory.getLogger(UserRelationController.class);

	@Autowired
	private UserRelationService userRelationService;

	@Operation(summary = "Create or update a user relation")
	@PostMapping("/create")
	public ResponseEntity<UserRelation> createRelation(@RequestBody UserRelationDto dto) {
		log.info("POST /api/user-relations/create - Creating relation type={}", dto.getRelationType());
		User userStub = new User();
		userStub.setId(dto.getUserId());
		User relatedUserStub = new User();
		relatedUserStub.setId(dto.getRelatedUserId());
		UserRelation userRelation = new UserRelation();
		userRelation.setUser(userStub);
		userRelation.setRelatedUser(relatedUserStub);
		userRelation.setRelationType(dto.getRelationType());
		userRelation.setComments(dto.getComments());
		UserRelation relation = userRelationService.createRelation(userRelation);
		log.info("POST /api/user-relations/create - Relation created with id={}", relation.getId());
		return ResponseEntity.ok(relation);
	}

	@Operation(summary = "Get all user relations")
	@GetMapping("/all")
	public ResponseEntity<List<UserRelation>> getAllRelations() {
		log.info("GET /api/user-relations/all - Fetching all relations");
		List<UserRelation> relations = userRelationService.getAllRelations();
		log.info("GET /api/user-relations/all - Returned {} relations", relations.size());
		return ResponseEntity.ok(relations);
	}

	@Operation(summary = "Get relations for a specific user")
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<UserRelation>> getRelationsByUser(@PathVariable Long userId) {
		log.info("GET /api/user-relations/user/{} - Fetching relations for user", userId);
		List<UserRelation> relations = userRelationService.getRelationsByUser(userId);
		log.info("GET /api/user-relations/user/{} - Returned {} relations", userId, relations.size());
		return ResponseEntity.ok(relations);
	}

	@Operation(summary = "Get user relations by type")
	@GetMapping("/user/{userId}/relation/{relationType}")
	public ResponseEntity<List<UserRelation>> getUserRelationsByType(@PathVariable Long userId,
			@PathVariable String relationType) {
		log.info("GET /api/user-relations/user/{}/relation/{} - Fetching relations by type", userId, relationType);
		List<UserRelation> relations = userRelationService.getUserRelationsByType(userId, relationType);
		log.info("GET /api/user-relations/user/{}/relation/{} - Returned {} relations", userId, relationType, relations.size());
		return ResponseEntity.ok(relations);
	}

	@Operation(summary = "Delete a user relation")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRelation(@PathVariable Long id) {
		log.info("DELETE /api/user-relations/{} - Deleting relation", id);
		userRelationService.deleteRelation(id);
		log.info("DELETE /api/user-relations/{} - Relation deleted", id);
		return ResponseEntity.noContent().build();
	}
}
