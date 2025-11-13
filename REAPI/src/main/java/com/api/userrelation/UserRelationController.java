package com.api.userrelation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-relations")
@RequiredArgsConstructor
public class UserRelationController {

	@Autowired
	private UserRelationService userRelationService;

	// 🔹 Create or update relation
	@PostMapping("/create")
	public ResponseEntity<UserRelation> createRelation(@RequestBody UserRelation userRelation) {

		UserRelation relation = userRelationService.createRelation(userRelation);
		return ResponseEntity.ok(relation);
	}

	// 🔹 Get all relations
	@GetMapping("/all")
	public ResponseEntity<List<UserRelation>> getAllRelations() {
		return ResponseEntity.ok(userRelationService.getAllRelations());
	}

	// 🔹 Get relations by user ID
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<UserRelation>> getRelationsByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(userRelationService.getRelationsByUser(userId));
	}

	

	// 🔹 Get relations by type (CLIENT_OF, DEALER_OF, etc.)
	@GetMapping("/user/{userId}/relation/{relationType}")
	public ResponseEntity<List<UserRelation>> getUserRelationsByType(@PathVariable Long userId,
			@PathVariable String relationType) {
		return ResponseEntity.ok(userRelationService.getUserRelationsByType(userId,relationType));
	}

	// 🔹 Delete a relation by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRelation(@PathVariable Long id) {
		userRelationService.deleteRelation(id);
		return ResponseEntity.noContent().build();
	}
}
