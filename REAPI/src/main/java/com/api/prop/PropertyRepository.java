package com.api.prop;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

	@Query("""
			    SELECT p FROM Property p
			    WHERE (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')))
			      AND (:type IS NULL OR LOWER(p.type) LIKE LOWER(CONCAT('%', :type, '%')))
			      AND (:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
			      AND (:minArea IS NULL OR p.superArea >= :minArea)
			      AND (:maxArea IS NULL OR p.superArea <= :maxArea)
			      AND (:minPrice IS NULL OR p.price >= :minPrice)
			      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
			      AND (:rentOrSale IS NULL OR LOWER(p.rentOrSale) = LOWER(:rentOrSale))
			      AND (:postDate IS NULL OR p.postDate >= :postDate)
			      AND (:postedByUser IS NULL OR p.postedByUser = :postedByUser)
			""")
	List<Property> search(String city, String type, String category, Double minArea, Double maxArea, Double minPrice,
			Double maxPrice, String rentOrSale, LocalDateTime postDate, Long postedByUser);

	@Query("""
			    SELECT DISTINCT p FROM Property p
			    WHERE (:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')))
			      AND (:address IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :address, '%')))
			      AND (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')))
			      AND (:type IS NULL OR LOWER(p.type) LIKE LOWER(CONCAT('%', :type, '%')))
			      AND (:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
			      AND (:postedBy IS NULL OR LOWER(p.postedBy) LIKE LOWER(CONCAT('%', :postedBy, '%')))
			      AND (:constructionStatus IS NULL OR LOWER(p.constructionStatus) LIKE LOWER(CONCAT('%', :constructionStatus, '%')))
			      AND (:currency IS NULL OR LOWER(p.currency) LIKE LOWER(CONCAT('%', :currency, '%')))
			      AND (:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')))
			      AND (:minPrice IS NULL OR p.price >= :minPrice)
			      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
			      AND (:minBedrooms IS NULL OR p.bedrooms >= :minBedrooms)
			      AND (:maxBedrooms IS NULL OR p.bedrooms <= :maxBedrooms)
			      AND (:minBathrooms IS NULL OR p.bathrooms >= :minBathrooms)
			      AND (:maxBathrooms IS NULL OR p.bathrooms <= :maxBathrooms)
			      AND (:minArea IS NULL OR p.superArea >= :minArea)
			      AND (:maxArea IS NULL OR p.superArea <= :maxArea)
			      AND (:rentOrSale IS NULL OR LOWER(p.rentOrSale) = LOWER(:rentOrSale))
			      AND (:postDate IS NULL OR p.postDate >= :postDate)
			      AND (:postedByUser IS NULL OR p.postedByUser = :postedByUser)
			      AND (
			        :amenity IS NULL
			        OR p.amenities = :amenity
			        OR p.amenities LIKE CONCAT(:amenity, ',%')
			        OR p.amenities LIKE CONCAT('%,', :amenity)
			        OR p.amenities LIKE CONCAT('%,', :amenity, ',%')
			   )
			""")
	List<Property> searchAll(String title, String address, String city, String type, String category, String postedBy,
			String constructionStatus, String currency, String location, Double minPrice, Double maxPrice,
			Integer minBedrooms, Integer maxBedrooms, Integer minBathrooms, Integer maxBathrooms, Double minArea,
			Double maxArea, String amenity, String rentOrSale, LocalDateTime postDate, Long postedByUser);

}
