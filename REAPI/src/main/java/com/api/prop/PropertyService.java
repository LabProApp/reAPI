package com.api.prop;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.commons.ResourceNotFoundException;
import com.api.documents.DocumentminDto;
import com.api.documents.DocumentsService;
import com.api.enums.MasterEnums;
import com.api.plan.PlanService;
import com.api.user.User;
import com.api.user.UserRepository;

@Service
public class PropertyService {

	private static final Logger log = LoggerFactory.getLogger(PropertyService.class);

	private final PropertyRepository repository;
	private final ModelMapper mapper;
	@Autowired
	private DocumentsService documentsService;
	@Autowired
	private PlanService planService;
	@Autowired
	private UserRepository userRepository;


	@Autowired
	public PropertyService(PropertyRepository repository, ModelMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	
	@Transactional
	public PropertyDto addProperty(PropertyDto propertyDto) {
		log.info("addProperty - Adding property: title={}, city={}, type={}",
				propertyDto.getTitle(), propertyDto.getCity(), propertyDto.getType());
		enforcePostingLimit(propertyDto.getPostedByUser());
		Property property = mapper.map(propertyDto, Property.class);
		Property saved = repository.save(property);
		log.info("addProperty - Property saved with id={}", saved.getId());
		return mapper.map(saved, PropertyDto.class);
	}

	/**
	 * Throws {@link PropertyLimitExceededException} if the user has already
	 * posted as many properties as their plan allows. Posting on behalf of
	 * a missing user / unknown plan is treated as BASIC (limit 0) and
	 * rejected.
	 *
	 * <p><b>Downgrade behavior (documented):</b> when a user's subscription
	 * expires and they're lazily downgraded to BASIC (limit 0) by
	 * {@link com.api.user.User#effectivePackage()}, their <em>existing</em>
	 * property listings stay live in search results — only <em>new</em>
	 * inserts are blocked here. This is intentional so a 250-listing
	 * DELUX user whose card declines doesn't lose their entire inventory
	 * overnight; renewing reinstates the cap and they can post again.</p>
	 */
	private void enforcePostingLimit(Long userId) {
		if (userId == null) {
			throw new PropertyLimitExceededException(
				"Posting user is required.",
				"BASIC", 0, 0);
		}
		// Pessimistic lock on the user row serialises concurrent posts by the
		// same user so two parallel requests can't both slip past the count
		// check at limit-1. The lock is released when the surrounding
		// @Transactional addProperty() commits.
		User user = userRepository.findByIdForUpdate(userId).orElse(null);
		MasterEnums.PackageEnum pkg = user != null ? user.effectivePackage() : null;
		int limit = planService.getPropertyLimit(pkg);
		long current = repository.countByPostedByUser(userId);
		String planName = pkg != null ? pkg.name() : "BASIC";
		if (current >= limit) {
			String reason = limit == 0
				? "Your current plan does not include property listings. " +
				  "Upgrade to Delux or Premium to post a property."
				: String.format(
					"You have reached your plan's posting limit (%d/%d). " +
					"Upgrade your plan to post more properties.",
					current, limit);
			log.warn("addProperty - REJECTED userId={} plan={} count={} limit={}",
					userId, planName, current, limit);
			throw new PropertyLimitExceededException(reason, planName, limit, (int) current);
		}
	}

	
	public List<PropertyDto> getAllProperties() {
		log.info("getAllProperties - Fetching all properties");
		List<Property> properties = repository.findAll();
		List<Long> ids = properties.stream().map(Property::getId).collect(Collectors.toList());
		Map<Long, List<DocumentminDto>> docsMap = documentsService.getminDocumentsByObjectIds("PROPERTY", ids);
		return properties.stream().map(p -> {
			PropertyDto dto = toDto(p);
			dto.setDocumentList(docsMap.getOrDefault(p.getId(), List.of()));
			return dto;
		}).collect(Collectors.toList());
	}

	
	public Optional<PropertyDto> getPropertyById(Long id) {
		log.info("getPropertyById - Fetching property id={}", id);
		return repository.findById(id).map(p -> {
			PropertyDto dto = toDto(p);
			dto.setDocumentList(documentsService.getminDocumentsByObject("PROPERTY", p.getId()));
			return dto;
		});
	}

	
	public List<PropertyDto> search(String city, String type, String category, Double minArea, Double maxArea,
			Double minPrice, Double maxPrice, String rentOrSale, LocalDateTime postDate, Long postedByUser) {
		log.info("search - Searching properties [city={}, type={}, category={}, rentOrSale={}, price={}-{}]",
				city, type, category, rentOrSale, minPrice, maxPrice);
		List<Property> results = repository.search(city, type, category, minArea, maxArea, minPrice, maxPrice, rentOrSale, postDate, postedByUser);
		List<Long> ids = results.stream().map(Property::getId).collect(Collectors.toList());
		Map<Long, List<DocumentminDto>> docsMap = documentsService.getminDocumentsByObjectIds("PROPERTY", ids);
		return results.stream().map(p -> {
			PropertyDto dto = toDto(p);
			dto.setDocumentList(docsMap.getOrDefault(p.getId(), List.of()));
			return dto;
		}).collect(Collectors.toList());
	}

	
	@Transactional
	public PropertyDto updateProperty(Long id, PropertyDto updatedDto) {
		log.info("updateProperty - Updating property id={}", id);
		Property updatedProperty = mapper.map(updatedDto, Property.class);

		Property saved = repository.findById(id).map(existing -> {

			// --- Basic Info ---
			existing.setTitle(updatedProperty.getTitle());
			existing.setDescription(updatedProperty.getDescription());
			existing.setProjectName(updatedProperty.getProjectName());
			existing.setAddress(updatedProperty.getAddress());
			existing.setCity(updatedProperty.getCity());
			existing.setState(updatedProperty.getState());
			existing.setType(updatedProperty.getType());
			existing.setCategory(updatedProperty.getCategory());
			existing.setRentOrSale(updatedProperty.getRentOrSale());
			existing.setPropertyStatus(updatedProperty.getPropertyStatus());
			existing.setVerified(updatedProperty.isVerified());

			// --- Pricing ---
			existing.setPrice(updatedProperty.getPrice());
			existing.setCurrency(updatedProperty.getCurrency());
			existing.setMonthlyRent(updatedProperty.getMonthlyRent());
			existing.setSecurityDeposit(updatedProperty.getSecurityDeposit());
			existing.setBrokerage(updatedProperty.getBrokerage());
			existing.setNegotiable(updatedProperty.getNegotiable());
			existing.setLoanAvailable(updatedProperty.getLoanAvailable());

			// --- Area & Rooms ---
			existing.setBedrooms(updatedProperty.getBedrooms());
			existing.setBathrooms(updatedProperty.getBathrooms());
			existing.setCarpetArea(updatedProperty.getCarpetArea());
			existing.setSuperArea(updatedProperty.getSuperArea());

			// --- Location ---
			existing.setLocation(updatedProperty.getLocation());
			existing.setLandmark(updatedProperty.getLandmark());
			existing.setLatitude(updatedProperty.getLatitude());
			existing.setLongitude(updatedProperty.getLongitude());
			existing.setFacing(updatedProperty.getFacing());

			// --- Building Info ---
			existing.setFloorNumber(updatedProperty.getFloorNumber());
			existing.setTotalFloors(updatedProperty.getTotalFloors());
			existing.setParkingCount(updatedProperty.getParkingCount());
			existing.setParkingType(updatedProperty.getParkingType());
			existing.setPropertyAge(updatedProperty.getPropertyAge());
			existing.setOwnershipType(updatedProperty.getOwnershipType());
			existing.setFurnishing(updatedProperty.getFurnishing());
			existing.setConstructionStatus(updatedProperty.getConstructionStatus());
			existing.setReadyDate(updatedProperty.getReadyDate());

			// --- Project / Builder ---
			existing.setBuilderName(updatedProperty.getBuilderName());
			existing.setReraApproved(updatedProperty.getReraApproved());
			existing.setReraNumber(updatedProperty.getReraNumber());

			// --- Tenant Rules ---
			existing.setPreferredTenants(updatedProperty.getPreferredTenants());
			existing.setPetsAllowed(updatedProperty.getPetsAllowed());
			existing.setNonVegAllowed(updatedProperty.getNonVegAllowed());
			existing.setLeaseDuration(updatedProperty.getLeaseDuration());
			existing.setNoticePeriod(updatedProperty.getNoticePeriod());
			existing.setMaintenanceIncluded(updatedProperty.getMaintenanceIncluded());

			// --- Meta ---
			existing.setPostedBy(updatedProperty.getPostedBy());
			existing.setContactNumber(updatedProperty.getContactNumber());

			// --- Stats (usually NOT updated from API, but safe if needed) ---
			existing.setViewsCount(updatedProperty.getViewsCount());
			existing.setShortListCount(updatedProperty.getShortListCount());

			// --- Amenities ---
			existing.setAmenities(updatedProperty.getAmenities());

			// --- Audit ---
			existing.setLastUpdatedTs(LocalDateTime.now());
			existing.setUpdatedBy(updatedProperty.getUpdatedBy());

			return repository.save(existing);

		}).orElseThrow(() -> {
			log.error("updateProperty - Property not found for id={}", id);
			return new ResourceNotFoundException("Property not found with id " + id);
		});

		log.info("updateProperty - Property id={} updated successfully", id);
		return mapper.map(saved, PropertyDto.class);
	}

	
	@Transactional
	public void deleteProperty(Long id) {
		log.info("deleteProperty - Deleting property id={}", id);
		if (!repository.existsById(id)) {
			log.error("deleteProperty - Property not found for id={}", id);
			throw new ResourceNotFoundException("Property not found with id " + id);
		}
		repository.deleteById(id);
		log.info("deleteProperty - Property id={} deleted", id);
	}

	
	public List<PropertyDto> advancedSearch(String title, String address, String city, String type, String category,
			String postedBy, String constructionStatus, String currency, String location, Double minPrice,
			Double maxPrice, Integer minBedrooms, Integer maxBedrooms, Integer minBathrooms, Integer maxBathrooms,
			Double minArea, Double maxArea, String amenity, String rentOrSale, LocalDateTime postDate,
			Long postedByUser, String state, String furnishing, String ownershipType, String preferredTenants,
			String availability, int page, int size) {

		String searchLocation = (location != null && !location.trim().isEmpty()) ? location.trim().toLowerCase() : null;
		String cityFilter   = (city  != null && !city.trim().isEmpty())  ? city.trim().toLowerCase()  : null;
		String stateFilter  = (state != null && !state.trim().isEmpty()) ? state.trim().toLowerCase() : null;

		// Multi-amenity: pass only the first token to the DB query for pre-filtering;
		// any additional selected amenities are applied in-memory below (AND semantics).
		String primaryAmenity = null;
		List<String> extraAmenities = List.of();
		if (amenity != null && !amenity.isBlank()) {
			String[] parts = amenity.split(",");
			primaryAmenity = parts[0].trim().isEmpty() ? null : parts[0].trim();
			if (parts.length > 1) {
				extraAmenities = Arrays.stream(parts, 1, parts.length)
						.map(String::trim)
						.filter(s -> !s.isEmpty())
						.collect(Collectors.toList());
			}
		}

		List<Property> results = repository.searchAll(title, address, cityFilter, stateFilter, type, category, postedBy,
				constructionStatus, furnishing, ownershipType, preferredTenants, availability, currency,
				searchLocation, minPrice, maxPrice, minBedrooms, maxBedrooms, minBathrooms, maxBathrooms,
				minArea, maxArea, primaryAmenity, rentOrSale, postDate, postedByUser,
				PageRequest.of(page, size));

		// Apply remaining amenities in-memory (AND logic: property must contain all).
		if (!extraAmenities.isEmpty()) {
			results = results.stream().filter(p -> {
				if (p.getAmenities() == null || p.getAmenities().isBlank()) return false;
				String pa = p.getAmenities().toLowerCase();
				return extraAmenities.stream().allMatch(a -> {
					String la = a.toLowerCase();
					return pa.equals(la)
							|| pa.startsWith(la + ",")
							|| pa.endsWith("," + la)
							|| pa.contains("," + la + ",");
				});
			}).collect(Collectors.toList());
		}

		List<Long> ids = results.stream().map(Property::getId).collect(Collectors.toList());
		Map<Long, List<DocumentminDto>> docsMap = documentsService.getminDocumentsByObjectIds("PROPERTY", ids);

		return results.stream().map(p -> {
			PropertyDto dto = toDto(p);
			dto.setDocumentList(docsMap.getOrDefault(p.getId(), List.of()));
			return dto;
		}).collect(Collectors.toList());
	}

	
	public PropertyDto toDto(Property p) {
		PropertyDto dto = new PropertyDto();
		dto.setId(p.getId());
		dto.setTitle(p.getTitle());
		dto.setAddress(p.getAddress());
		dto.setCity(p.getCity());
		dto.setState(p.getState());
		dto.setPropertyStatus(p.getPropertyStatus());
		dto.setType(p.getType());
		dto.setCategory(p.getCategory());
		dto.setRentOrSale(p.getRentOrSale());
		dto.setVerified(p.isVerified());
		dto.setPrice(p.getPrice());
		dto.setCurrency(p.getCurrency());
		dto.setMonthlyRent(p.getMonthlyRent());
		dto.setSecurityDeposit(p.getSecurityDeposit());
		dto.setBrokerage(p.getBrokerage());
		dto.setNegotiable(p.getNegotiable());
		dto.setLoanAvailable(p.getLoanAvailable());
		dto.setBedrooms(p.getBedrooms());
		dto.setBathrooms(p.getBathrooms());
		dto.setCarpetArea(p.getCarpetArea());
		dto.setSuperArea(p.getSuperArea());
		dto.setLocation(p.getLocation());
		dto.setLandmark(p.getLandmark());
		dto.setLatitude(p.getLatitude());
		dto.setLongitude(p.getLongitude());
		dto.setFacing(p.getFacing());
		dto.setFloorNumber(p.getFloorNumber());
		dto.setTotalFloors(p.getTotalFloors());
		dto.setParkingCount(p.getParkingCount());
		dto.setParkingType(p.getParkingType());
		dto.setPropertyAge(p.getPropertyAge());
		dto.setOwnershipType(p.getOwnershipType());
		dto.setFurnishing(p.getFurnishing());
		dto.setConstructionStatus(p.getConstructionStatus());
		dto.setReadyDate(p.getReadyDate());
		dto.setProjectName(p.getProjectName());
		dto.setBuilderName(p.getBuilderName());
		dto.setReraApproved(p.getReraApproved());
		dto.setReraNumber(p.getReraNumber());
		dto.setPreferredTenants(p.getPreferredTenants());
		dto.setPetsAllowed(p.getPetsAllowed());
		dto.setNonVegAllowed(p.getNonVegAllowed());
		dto.setLeaseDuration(p.getLeaseDuration());
		dto.setNoticePeriod(p.getNoticePeriod());
		dto.setMaintenanceIncluded(p.getMaintenanceIncluded());
		dto.setPostedBy(p.getPostedBy());
		dto.setPostedByUser(p.getPostedByUser());
		dto.setPostDate(p.getPostDate());
		dto.setContactNumber(p.getContactNumber());
		dto.setDescription(p.getDescription());
		dto.setAmenities(p.getAmenities());
		dto.setViewsCount(p.getViewsCount());
		dto.setShortListCount(p.getShortListCount());
		// audit / base fields
		dto.setCode(p.getCode());
		dto.setCreatedTs(p.getCreatedTs());
		dto.setLastUpdatedTs(p.getLastUpdatedTs());
		dto.setCreatedBy(p.getCreatedBy());
		dto.setUpdatedBy(p.getUpdatedBy());
		return dto;
	}
}
