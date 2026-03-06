package com.api.prop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import com.api.commons.ResourceNotFoundException;
import com.api.documents.DocumentminDto;
import com.api.documents.DocumentsService;

@Service
public class PropertyService {

	private final PropertyRepository repository;
	private final ModelMapper mapper;
	@Autowired
	private DocumentsService documentsService;

	@Autowired
	public PropertyService(PropertyRepository repository, ModelMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	/** Add a new property */
	public PropertyDto addProperty(PropertyDto propertyDto) {
		Property property = mapper.map(propertyDto, Property.class);

		Property saved = repository.save(property);
		return mapper.map(saved, PropertyDto.class);
	}

	/** Get all properties */
	public List<PropertyDto> getAllProperties() {
		return repository.findAll().stream().map(p -> mapper.map(p, PropertyDto.class)).collect(Collectors.toList());
	}

	/** Get property by ID */
	public Optional<PropertyDto> getPropertyById(Long id) {
		return repository.findById(id).map(p -> mapper.map(p, PropertyDto.class));
	}

	/** Search properties dynamically */
	public List<PropertyDto> search(String city, String type, String category, Double minArea, Double maxArea,
			Double minPrice, Double maxPrice, String rentOrSale, LocalDateTime postDate, Long postedByUser) {

		return repository
				.search(city, type, category, minArea, maxArea, minPrice, maxPrice, rentOrSale, postDate, postedByUser)
				.stream().map(property -> {
					PropertyDto dto = mapper.map(property, PropertyDto.class);

					// Map documents
					List<DocumentminDto> documentDtos = documentsService.getminDocumentsByObject("PROPERTY",
							dto.getId());
					
					dto.setDocumentList(documentDtos);
					return dto;
				}).collect(Collectors.toList());
	}

	/** Update an existing property */
	public PropertyDto updateProperty(Long id, PropertyDto updatedDto) {
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

		    // ❌ DO NOT update these:
		    // existing.setPostDate(...)
		    // existing.setPostedByUser(...)
		    // existing.setId(...)

		    return repository.save(existing);

		}).orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + id));

		return mapper.map(saved, PropertyDto.class);
	}

	/** Delete a property */
	public void deleteProperty(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Property not found with id " + id);
		}
		repository.deleteById(id);
	}

	/** Advanced search */
	public List<PropertyDto> advancedSearch(String title, String address, String city, String type, String category,
			String postedBy, String constructionStatus, String currency, String location, Double minPrice,
			Double maxPrice, Integer minBedrooms, Integer maxBedrooms, Integer minBathrooms, Integer maxBathrooms,
			Double minArea, Double maxArea, String amenity, String rentOrSale, LocalDateTime postDate,
			Long postedByUser) {
		return repository
				.searchAll(title, address, city, type, category, postedBy, constructionStatus, currency, location,
						minPrice, maxPrice, minBedrooms, maxBedrooms, minBathrooms, maxBathrooms, minArea, maxArea,
						amenity, rentOrSale, postDate, postedByUser)
				.stream().map(p -> mapper.map(p, PropertyDto.class)).collect(Collectors.toList());
	}

	/** Get properties posted by a specific user */
	// Use Advance Search API
}
