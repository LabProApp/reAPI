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
			existing.setTitle(updatedProperty.getTitle());
			existing.setDescription(updatedProperty.getDescription());
			existing.setProjectName(updatedProperty.getProjectName());
			existing.setAddress(updatedProperty.getAddress());
			existing.setCity(updatedProperty.getCity());
			existing.setType(updatedProperty.getType());
			existing.setPrice(updatedProperty.getPrice());
			existing.setBedrooms(updatedProperty.getBedrooms());
			existing.setBathrooms(updatedProperty.getBathrooms());
			existing.setLocation(updatedProperty.getLocation());
			existing.setCarpetArea(updatedProperty.getCarpetArea());
			existing.setSuperArea(updatedProperty.getSuperArea());
			existing.setAmenities(updatedProperty.getAmenities());
			existing.setPostedBy(updatedProperty.getPostedBy());
			existing.setConstructionStatus(updatedProperty.getConstructionStatus());
			existing.setPropertyStatus(updatedProperty.getPropertyStatus());
			existing.setPlanPackage(updatedProperty.getPlanPackage());
			existing.setCurrency(updatedProperty.getCurrency());
			existing.setReadyDate(updatedProperty.getReadyDate());
			existing.setCategory(updatedProperty.getCategory());
			existing.setRentOrSale(updatedProperty.getRentOrSale());
			existing.setPostDate(updatedProperty.getPostDate());
			existing.setPostedByUser(updatedProperty.getPostedByUser());

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
