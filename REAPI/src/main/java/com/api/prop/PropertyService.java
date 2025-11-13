package com.api.prop;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.ResourceNotFoundException;

@Service
public class PropertyService {

	@Autowired
	private PropertyRepository repository;

	/** Add a new property */
	public Property addProperty(Property property) {
		return repository.save(property);
	}

	/** Get all properties */
	public List<Property> getAllProperties() {
		return repository.findAll();
	}

	/** Get property by ID */
	public Optional<Property> getPropertyById(Long id) {
		return repository.findById(id);
	}

	/** Search properties dynamically */
	public List<Property> search(String city, String type, String category, Double minArea, Double maxArea,
			Double minPrice, Double maxPrice, String rentOrSale, LocalDateTime postDate) {
		return repository.search(city, type, category, minArea, maxArea, minPrice, maxPrice, rentOrSale, postDate);
	}

	/** Update an existing property */
	public Property updateProperty(Long id, Property updated) {
		return repository.findById(id).map(existing -> {
			existing.setTitle(updated.getTitle());
			existing.setDescription(updated.getDescription());
			existing.setProjectName(updated.getProjectName());
			existing.setAddress(updated.getAddress());
			existing.setCity(updated.getCity());
			existing.setType(updated.getType());
			existing.setPrice(updated.getPrice());
			existing.setBedrooms(updated.getBedrooms());
			existing.setBathrooms(updated.getBathrooms());
			existing.setLocation(updated.getLocation());
			existing.setArea(updated.getArea());
			existing.setAmenities(updated.getAmenities());
			existing.setPostedBy(updated.getPostedBy());
			existing.setConstructionStatus(updated.getConstructionStatus());
			existing.setCurrency(updated.getCurrency());
			existing.setReadyDate(updated.getReadyDate());
			existing.setCategory(updated.getCategory());
			existing.setRentOrSale(updated.getRentOrSale());
			existing.setPostDate(updated.getPostDate());
			existing.setPostedByUser(updated.getPostedByUser());

			return repository.save(existing);
		}).orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + id));
	}

	/** Delete a property */
	public void deleteProperty(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Property not found with id " + id);
		}
		repository.deleteById(id);
	}

	public List<Property> advancedSearch(String title, String address, String city, String type, String category,
			String postedBy, String constructionStatus, String currency, String location, Double minPrice,
			Double maxPrice, Integer minBedrooms, Integer maxBedrooms, Integer minBathrooms, Integer maxBathrooms,
			Double minArea, Double maxArea, String amenity, String rentOrSale, LocalDateTime postDate) {
		return repository.searchAll(title, address, city, type, category, postedBy, constructionStatus, currency,
				location, minPrice, maxPrice, minBedrooms, maxBedrooms, minBathrooms, maxBathrooms, minArea, maxArea,
				amenity, rentOrSale, postDate);
	}

	public Property uploadDocuments(Long propertyId, List<MultipartFile> files, List<String> captions)
			throws IOException {
		Property property = repository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found"));

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String caption = captions.size() > i ? captions.get(i) : null;

			// Upload to S3 or local storage
			String url = "test"; // s3Service.uploadFile(file);

			Documents doc = Documents.builder().docUrl(url).docType(file.getContentType()).caption(caption)
					.property(property).docCategory("PROPERTY").build();

			property.getDocuments().add(doc);
		}

		return repository.save(property);
	}

}
