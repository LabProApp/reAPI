package com.api.prop;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/property")
@Tag(name = "Property APIs", description = "Operations related to Property management")
public class PropertyController {

	private final PropertyService service;

	public PropertyController(PropertyService service) {
		this.service = service;
	}

	@PostMapping("/add")
	public ResponseEntity<Property> addProperty(@Valid @RequestBody Property property) {
		return ResponseEntity.ok(service.addProperty(property));
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Property> update(@PathVariable Long id, @Valid @RequestBody Property property) {
		return ResponseEntity.ok(service.updateProperty(id, property));
	}

	@GetMapping("/getall")
	public ResponseEntity<List<Property>> getAll() {
		return ResponseEntity.ok(service.getAllProperties());
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<Property> getById(@PathVariable Long id) {
		Property property = service.getPropertyById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
		return ResponseEntity.ok(property);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Property>> search(@RequestParam(required = false) String city,
			@RequestParam(required = false) String type, @RequestParam(required = false) String category,
			@RequestParam(required = false) Double minArea, @RequestParam(required = false) Double maxArea,
			@RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) String rentOrSale, @RequestParam(required = false) LocalDateTime postDate) {
		return ResponseEntity
				.ok(service.search(city, type, category, minArea, maxArea, minPrice, maxPrice, rentOrSale, postDate));

	}

	@GetMapping("/advancedsearch")
	public ResponseEntity<List<Property>> advancedSearch(@RequestParam(required = false) String title,
			@RequestParam(required = false) String address, @RequestParam(required = false) String city,
			@RequestParam(required = false) String type, @RequestParam(required = false) String category,
			@RequestParam(required = false) String postedBy, @RequestParam(required = false) String constructionStatus,
			@RequestParam(required = false) String currency, @RequestParam(required = false) String location,
			@RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) Integer minBedrooms, @RequestParam(required = false) Integer maxBedrooms,
			@RequestParam(required = false) Integer minBathrooms, @RequestParam(required = false) Integer maxBathrooms,
			@RequestParam(required = false) Double minArea, @RequestParam(required = false) Double maxArea,
			@RequestParam(required = false) String amenity, @RequestParam(required = false) String rentOrSale,
			@RequestParam(required = false) LocalDateTime postDate) {
		return ResponseEntity.ok(service.advancedSearch(title, address, city, type, category, postedBy,
				constructionStatus, currency, location, minPrice, maxPrice, minBedrooms, maxBedrooms, minBathrooms,
				maxBathrooms, minArea, maxArea, amenity, rentOrSale, postDate));

	}

	@PostMapping("/upload-document/{propertyId}")
	public ResponseEntity<Property> uploadDocuments(
	        @PathVariable Long propertyId,
	        @RequestParam("files") List<MultipartFile> files,
	        @RequestParam("captions") List<String> captions) throws IOException {

	    return ResponseEntity.ok(service.uploadDocuments(propertyId, files, captions));
	}
	
	/*
	 * @GetMapping("/upload-document/{propertyId}") public
	 * ResponseEntity<List<Property>> uploadDocuments(@PathVariable Long propertyId,
	 * 
	 * @RequestParam(value = "files") List<MultipartFile> files) throws IOException
	 * {
	 * 
	 * return ResponseEntity.ok(service.uploadDocuments(propertyId, files));
	 * 
	 * }
	 */
	

}
