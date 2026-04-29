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
import com.api.notifications.NotificationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing property management endpoints under
 * {@code /api/property}.
 *
 * <p>Provides operations to create, read, update, search, and share real
 * estate listings. All search parameters are optional; omitting a parameter
 * removes that filter from the query. The share endpoint dispatches a
 * multi-channel notification (email / SMS / WhatsApp) via
 * {@link NotificationService}.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/property")
@Tag(name = "Property APIs", description = "Operations related to Property management")
public class PropertyController {

	@Autowired
	private PropertyService service;

	@Autowired
	private NotificationService notificationService;

	/**
	 * Creates a new property listing.
	 *
	 * @param propertyDto the validated listing details from the request body
	 * @return {@code 200 OK} with the persisted {@link PropertyDto} including
	 *         the generated ID
	 */
	@PostMapping("/add")
	public ResponseEntity<PropertyDto> addProperty(@Valid @RequestBody PropertyDto propertyDto) {
		log.info("POST /api/property/add - Adding property: title={}, city={}, type={}",
				propertyDto.getTitle(), propertyDto.getCity(), propertyDto.getType());
		PropertyDto savedProperty = service.addProperty(propertyDto);
		log.info("POST /api/property/add - Property created with id={}", savedProperty.getId());
		return ResponseEntity.ok(savedProperty);
	}

	/**
	 * Updates an existing property listing.
	 *
	 * @param id          the ID of the property to update
	 * @param propertyDto the validated updated listing details from the request body
	 * @return {@code 200 OK} with the updated {@link PropertyDto}
	 * @throws ResourceNotFoundException if no property exists with the given ID
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<PropertyDto> update(@PathVariable Long id, @Valid @RequestBody PropertyDto propertyDto) {
		log.info("PUT /api/property/update/{} - Updating property", id);
		PropertyDto updatedProperty = service.updateProperty(id, propertyDto);
		log.info("PUT /api/property/update/{} - Property updated", id);
		return ResponseEntity.ok(updatedProperty);
	}

	/**
	 * Retrieves all property listings with their associated documents.
	 *
	 * @return {@code 200 OK} with a list of all {@link PropertyDto} objects
	 */
	@GetMapping("/getall")
	public ResponseEntity<List<PropertyDto>> getAll() {
		log.info("GET /api/property/getall - Fetching all properties");
		List<PropertyDto> properties = service.getAllProperties();
		log.info("GET /api/property/getall - Returned {} properties", properties.size());
		return ResponseEntity.ok(properties);
	}

	/**
	 * Retrieves a single property by its ID.
	 *
	 * @param id the property ID to look up
	 * @return {@code 200 OK} with the matching {@link PropertyDto}
	 * @throws ResourceNotFoundException if no property exists with the given ID
	 */
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

	/**
	 * Performs a basic filtered search across property listings.
	 *
	 * <p>All query parameters are optional. Omitting a parameter removes that
	 * filter. Documents are included in each result.</p>
	 *
	 * @param city          filter by city (partial match, case-insensitive)
	 * @param type          filter by property type (partial match, case-insensitive)
	 * @param category      filter by category (partial match, case-insensitive)
	 * @param minArea       minimum super area in sq ft
	 * @param maxArea       maximum super area in sq ft
	 * @param minPrice      minimum price
	 * @param maxPrice      maximum price
	 * @param rentOrSale    rent/sale indicator (exact match, case-insensitive)
	 * @param postDate      return only listings posted on or after this date-time
	 * @param postedByUser  filter by the ID of the posting user
	 * @return {@code 200 OK} with the list of matching {@link PropertyDto} objects
	 */
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

	/**
	 * Performs an advanced multi-criteria search across property listings.
	 *
	 * <p>Supports all basic search filters plus bedroom/bathroom ranges, a global
	 * location keyword, amenity, and more. All query parameters are optional.</p>
	 *
	 * @param title               filter by title (partial match, case-insensitive)
	 * @param address             filter by address (partial match, case-insensitive)
	 * @param city                filter by city (partial match, case-insensitive)
	 * @param type                filter by property type (partial match, case-insensitive)
	 * @param category            filter by category (partial match, case-insensitive)
	 * @param postedBy            filter by poster type (partial match, case-insensitive)
	 * @param constructionStatus  filter by construction status (partial match, case-insensitive)
	 * @param currency            filter by currency code (partial match, case-insensitive)
	 * @param location            global location keyword matched across multiple fields
	 * @param minPrice            minimum price
	 * @param maxPrice            maximum price
	 * @param minBedrooms         minimum bedroom count
	 * @param maxBedrooms         maximum bedroom count
	 * @param minBathrooms        minimum bathroom count
	 * @param maxBathrooms        maximum bathroom count
	 * @param minArea             minimum super area in sq ft
	 * @param maxArea             maximum super area in sq ft
	 * @param amenity             single amenity ID to filter by
	 * @param rentOrSale          rent/sale indicator (exact match, case-insensitive)
	 * @param postDate            return only listings posted on or after this date-time
	 * @param postedByUser        filter by the ID of the posting user
	 * @return {@code 200 OK} with the list of matching {@link PropertyDto} objects
	 */
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
			@RequestParam(required = false) LocalDateTime postDate, @RequestParam(required = false) Long postedByUser) {
		log.info("GET /api/property/advancedsearch - Advanced search [city={}, type={}, location={}, beds={}-{}, price={}-{}]",
				city, type, location, minBedrooms, maxBedrooms, minPrice, maxPrice);
		List<PropertyDto> results = service.advancedSearch(title, address, city, type, category, postedBy,
				constructionStatus, currency, location, minPrice, maxPrice, minBedrooms, maxBedrooms, minBathrooms,
				maxBathrooms, minArea, maxArea, amenity, rentOrSale, postDate, postedByUser);
		log.info("GET /api/property/advancedsearch - Returned {} results", results.size());
		return ResponseEntity.ok(results);
	}

	/**
	 * Shares a property listing with a recipient via email, SMS, or WhatsApp.
	 *
	 * <p>Exactly one of {@code toEmail} or {@code toMobile} must be supplied in
	 * the request body (validated by {@link SharePropertyRequest#isAtLeastOneRecipient()}).
	 * The notification is dispatched asynchronously through
	 * {@link NotificationService#notifyPropertyShared}.</p>
	 *
	 * @param id      the ID of the property to share
	 * @param request the validated share request containing recipient and channel details
	 * @return {@code 200 OK} with an empty body after the notification is dispatched
	 * @throws ResourceNotFoundException if no property exists with the given ID
	 */
	@PostMapping("/{id}/share")
	public ResponseEntity<Void> shareProperty(@PathVariable Long id,
			@Valid @RequestBody SharePropertyRequest request) {
		log.info("POST /api/property/{}/share - Sharing with to={}", id,
				request.getToEmail() != null ? request.getToEmail() : request.getToMobile());
		PropertyDto property = service.getPropertyById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
		notificationService.notifyPropertyShared(property, request);
		log.info("POST /api/property/{}/share - Share notification dispatched", id);
		return ResponseEntity.ok().build();
	}

}
