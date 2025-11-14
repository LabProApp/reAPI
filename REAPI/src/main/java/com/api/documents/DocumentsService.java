package com.api.documents;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api.prop.Property;
import com.api.prop.PropertyRepository;
import com.api.user.User;
import com.api.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentsService {

	@Autowired
	private DocumentRepository documentsRepository;
	@Autowired
	private PropertyRepository propertyRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private S3Service s3Service;

	public Documents uploadDocument(MultipartFile file, String docType, String caption, Long propertyId, Long userId)
			throws IOException {

		String folderName = "documents";

		String s3Url = s3Service.uploadFile(file, folderName);

		Documents doc = new Documents();
		doc.setDocUrl(s3Url);
		doc.setDocType(docType);
		doc.setCaption(caption);

		if (propertyId != null) {
			Property property = propertyRepository.findById(propertyId)
					.orElseThrow(() -> new RuntimeException("Property not found"));
			doc.setDocCategory("PROPERTY");
			doc.setProperty(property);
		}

		if (userId != null) {
			User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
			doc.setDocCategory("USER");
			doc.setUser(user);
		}

		return documentsRepository.save(doc);
	}

	public void deleteDocument(Long id) {
		Documents doc = documentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));

		s3Service.deleteFile(doc.getDocUrl());
		documentsRepository.delete(doc);
	}

	public List<Documents> getByProperty(Long id) {
		return documentsRepository.findByPropertyId(id);
	}

	public List<Documents> getByUser(Long id) {
		return documentsRepository.findByUserId(id);
	}

	public List<Documents> getAll() {
		return documentsRepository.findAll();
	}

	public Documents get(Long id) {
		return documentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
	}
}
