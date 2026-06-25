package com.api.prop;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.context.ApplicationEventPublisher;

import com.api.commons.ResourceNotFoundException;
import com.api.notifications.events.PropertySharedEvent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/property")
@Tag(name = "Property APIs", description = "Operations related to Property management")
public class PropertyController {

	private static final Logger log = LoggerFactory.getLogger(PropertyController.class);

	@Autowired
	private PropertyService service;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Operation(summary = "Add a new property listing")
	@PostMapping("/add")
	public ResponseEntity<PropertyDto> addProperty(@Valid @RequestBody PropertyDto propertyDto) {
		// Force postedByUser to the JWT subject so a client can't pass someone
		// else's userId to have their plan limit checked.
		Long jwtUserId = com.api.security.AuthUtils.currentUserId();
		if (jwtUserId == null) {
			return ResponseEntity.status(401).build();
		}
		propertyDto.setPostedByUser(jwtUserId);

		log.info("POST /api/property/add - userId={} title={} city={} type={}",
				jwtUserId, propertyDto.getTitle(), propertyDto.getCity(), propertyDto.getType());
		PropertyDto savedProperty = service.addProperty(propertyDto);
		log.info("POST /api/property/add - Property created with id={}", savedProperty.getId());
		return ResponseEntity.ok(savedProperty);
	}

	@Operation(summary = "Update an existing property listing")
	@PutMapping("/update/{id}")
	public ResponseEntity<PropertyDto> update(@PathVariable Long id, @Valid @RequestBody PropertyDto propertyDto) {
		Long jwtUserId = com.api.security.AuthUtils.currentUserId();
		if (jwtUserId == null) {
			return ResponseEntity.status(401).build();
		}
		PropertyDto existing = service.getPropertyById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
		boolean isAdmin = com.api.security.AuthUtils.hasRole("ADMIN");
		if (!isAdmin && !jwtUserId.equals(existing.getPostedByUser())) {
			log.warn("PUT /api/property/update/{} - Forbidden: jwtUserId={} not owner", id, jwtUserId);
			return ResponseEntity.status(403).build();
		}
		// Prevent privilege escalation: keep the original owner
		propertyDto.setPostedByUser(existing.getPostedByUser());
		log.info("PUT /api/property/update/{} - Updating property by userId={}", id, jwtUserId);
		PropertyDto updatedProperty = service.updateProperty(id, propertyDto);
		log.info("PUT /api/property/update/{} - Property updated", id);
		return ResponseEntity.ok(updatedProperty);
	}

	@Operation(summary = "Delete a property listing")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Long jwtUserId = com.api.security.AuthUtils.currentUserId();
		if (jwtUserId == null) {
			return ResponseEntity.status(401).build();
		}
		PropertyDto existing = service.getPropertyById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
		boolean isAdmin = com.api.security.AuthUtils.hasRole("ADMIN");
		if (!isAdmin && !jwtUserId.equals(existing.getPostedByUser())) {
			log.warn("DELETE /api/property/delete/{} - Forbidden: jwtUserId={} not owner", id, jwtUserId);
			return ResponseEntity.status(403).build();
		}
		log.info("DELETE /api/property/delete/{} - Deleting property by userId={}", id, jwtUserId);
		service.deleteProperty(id);
		log.info("DELETE /api/property/delete/{} - Deleted", id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Get all property listings")
	@GetMapping("/getall")
	public ResponseEntity<List<PropertyDto>> getAll() {
		log.info("GET /api/property/getall - Fetching all properties");
		List<PropertyDto> properties = service.getAllProperties();
		log.info("GET /api/property/getall - Returned {} properties", properties.size());
		return ResponseEntity.ok(properties);
	}

	@Operation(summary = "Get property by ID")
	@GetMapping("/get/{id}")
	public ResponseEntity<PropertyDto> getById(@PathVariable Long id) {
		log.info("GET /api/property/get/{} - Fetching property by id", id);
		PropertyDto property = service.getPropertyById(id)
				.orElseThrow(() -> {
					log.warn("GET /api/property/get/{} - Property not found", id);
					return new ResourceNotFoundException("Property not found with id: " + id);
				});
		log.info("GET /api/property/get/{} - Property fetched: title={}", id, property.getTitle());
		return ResponseEntity.ok(property);
	}

	@Operation(summary = "Search properties with basic filters")
	@GetMapping("/search")
	public ResponseEntity<List<PropertyDto>> search(@RequestParam(required = false) String city,
			@RequestParam(required = false) String type, @RequestParam(required = false) String category,
			@RequestParam(required = false) Double minArea, @RequestParam(required = false) Double maxArea,
			@RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) String rentOrSale, @RequestParam(required = false) LocalDateTime postDate,
			@RequestParam(required = false) Long postedByUser) {
		log.info("GET /api/property/search - Search params [city={}, type={}, category={}, rentOrSale={}, price={}-{}]",
				city, type, category, rentOrSale, minPrice, maxPrice);
		List<PropertyDto> results = service.search(city, type, category, minArea, maxArea, minPrice, maxPrice,
				rentOrSale, postDate, postedByUser);
		log.info("GET /api/property/search - Returned {} results", results.size());
		return ResponseEntity.ok(results);
	}

	@Operation(summary = "Advanced property search with multiple filters")
	@GetMapping("/advancedsearch")
	public ResponseEntity<List<PropertyDto>> advancedSearch(
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String address,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String state,
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String postedBy,
			@RequestParam(required = false) String constructionStatus,
			@RequestParam(required = false) String furnishing,
			@RequestParam(required = false) String ownershipType,
			@RequestParam(required = false) String preferredTenants,
			@RequestParam(required = false) String availability,
			@RequestParam(required = false) String currency,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) Integer minBedrooms,
			@RequestParam(required = false) Integer maxBedrooms,
			@RequestParam(required = false) Integer minBathrooms,
			@RequestParam(required = false) Integer maxBathrooms,
			@RequestParam(required = false) Double minArea,
			@RequestParam(required = false) Double maxArea,
			@RequestParam(required = false) String amenity,
			@RequestParam(required = false) String rentOrSale,
			@RequestParam(required = false) LocalDateTime postDate,
			@RequestParam(required = false) Long postedByUser,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size) {
		log.info("GET /api/property/advancedsearch - [city={}, state={}, type={}, location={}, beds={}-{}, price={}-{}, furnishing={}, ownership={}, tenants={}, page={}, size={}]",
				city, state, type, location, minBedrooms, maxBedrooms, minPrice, maxPrice, furnishing, ownershipType, preferredTenants, page, size);
		List<PropertyDto> results = service.advancedSearch(title, address, city, type, category, postedBy,
				constructionStatus, currency, location, minPrice, maxPrice, minBedrooms, maxBedrooms, minBathrooms,
				maxBathrooms, minArea, maxArea, amenity, rentOrSale, postDate, postedByUser,
				state, furnishing, ownershipType, preferredTenants, availability, page, size);
		log.info("GET /api/property/advancedsearch - Returned {} results (page={}, size={})", results.size(), page, size);
		return ResponseEntity.ok(results);
	}

	@Operation(summary = "Share a property with a recipient via email, SMS, or WhatsApp")
	@PostMapping("/{id}/share")
	public ResponseEntity<Void> shareProperty(@PathVariable Long id,
			@Valid @RequestBody SharePropertyRequest request) {
		log.info("POST /api/property/{}/share - Sharing with to={}", id,
				request.getToEmail() != null ? request.getToEmail() : request.getToMobile());
		PropertyDto property = service.getPropertyById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
		eventPublisher.publishEvent(new PropertySharedEvent(
				request.getSenderName(), request.getToEmail(), request.getToMobile(),
				request.isSendWhatsApp(),
				property.getTitle(), property.getAddress(), property.getLocation(),
				property.getCity(), property.getState(), property.getPrice(),
				property.getRentOrSale(), property.getBedrooms(), property.getBathrooms(),
				property.getType(), property.getCarpetArea(), property.getConstructionStatus(),
				property.getContactNumber(), property.getDescription()));
		log.info("POST /api/property/{}/share - PropertySharedEvent published", id);
		return ResponseEntity.ok().build();
	}

}
