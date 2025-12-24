package com.api.prop;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.commons.ResourceNotFoundException;
import com.api.documents.DocumentsService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/property")
@Tag(name = "Property APIs", description = "Operations related to Property management")
public class PropertyController {

	@Autowired
	private PropertyService service;
	

	@PostMapping("/add")
	public ResponseEntity<PropertyDto> addProperty(@Valid @RequestBody PropertyDto propertyDto) {
		PropertyDto savedProperty = service.addProperty(propertyDto);
		return ResponseEntity.ok(savedProperty);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<PropertyDto> update(@PathVariable Long id, @Valid @RequestBody PropertyDto propertyDto) {
		PropertyDto updatedProperty = service.updateProperty(id, propertyDto);
		return ResponseEntity.ok(updatedProperty);
	}

	@GetMapping("/getall")
	public ResponseEntity<List<PropertyDto>> getAll() {
		List<PropertyDto> properties = service.getAllProperties();
		return ResponseEntity.ok(properties);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<PropertyDto> getById(@PathVariable Long id) {
		PropertyDto property = service.getPropertyById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
		return ResponseEntity.ok(property);
	}

	@GetMapping("/search")
	public ResponseEntity<List<PropertyDto>> search(@RequestParam(required = false) String city,
			@RequestParam(required = false) String type, @RequestParam(required = false) String category,
			@RequestParam(required = false) Double minArea, @RequestParam(required = false) Double maxArea,
			@RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) String rentOrSale, @RequestParam(required = false) LocalDateTime postDate,
			@RequestParam(required = false) Long postedByUser

	) {
		List<PropertyDto> results = service.search(city, type, category, minArea, maxArea, minPrice, maxPrice,
				rentOrSale, postDate, postedByUser);
		return ResponseEntity.ok(results);
	}

	@GetMapping("/advancedsearch")
	public ResponseEntity<List<PropertyDto>> advancedSearch(@RequestParam(required = false) String title,
			@RequestParam(required = false) String address, @RequestParam(required = false) String city,
			@RequestParam(required = false) String type, @RequestParam(required = false) String category,
			@RequestParam(required = false) String postedBy, @RequestParam(required = false) String constructionStatus,
			@RequestParam(required = false) String currency, @RequestParam(required = false) String location,
			@RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) Integer minBedrooms, @RequestParam(required = false) Integer maxBedrooms,
			@RequestParam(required = false) Integer minBathrooms, @RequestParam(required = false) Integer maxBathrooms,
			@RequestParam(required = false) Double minArea, @RequestParam(required = false) Double maxArea,
			@RequestParam(required = false) String amenity, @RequestParam(required = false) String rentOrSale,
			@RequestParam(required = false) LocalDateTime postDate, Long postedByUser) {
		List<PropertyDto> results = service.advancedSearch(title, address, city, type, category, postedBy,
				constructionStatus, currency, location, minPrice, maxPrice, minBedrooms, maxBedrooms, minBathrooms,
				maxBathrooms, minArea, maxArea, amenity, rentOrSale, postDate, postedByUser);
		return ResponseEntity.ok(results);
	}

}
