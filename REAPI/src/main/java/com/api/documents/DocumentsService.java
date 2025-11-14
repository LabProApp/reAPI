package com.api.documents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api.prop.PropertyRepository;
import com.api.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentsService {

	@Autowired
	private DocumentRepository documentsRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PropertyRepository propertyRepository;
	@Autowired
	private S3Service s3Service;

	public List<Documents> uploadDocuments(String objectType, Long objectId, List<MultipartFile> files,
			List<String> captions) throws IOException {

		List<Documents> savedDocs = new ArrayList<>();

		for (int i = 0; i < files.size(); i++) {

			MultipartFile file = files.get(i);
			String caption = (captions != null && captions.size() > i) ? captions.get(i) : null;
			String contentType = file.getContentType();
			String docType = DocTypeDetector.detect(contentType);
			Documents doc = uploadDocument(file, docType, caption, objectType, objectId);
			savedDocs.add(doc);
		}

		return savedDocs;
	}

	public Documents uploadDocument(MultipartFile file, String docType, String caption, String objectType,
			Long objectId) throws IOException {

		objectType = objectType.toUpperCase();

		String folderName = objectType;
		String s3Url = s3Service.uploadFile(file, folderName);
		Documents doc = new Documents();
		doc.setDocUrl(s3Url);
		doc.setDocType(docType);
		doc.setCaption(caption);
		doc.setObjectType(objectType);
		doc.setObjectId(objectId);
		return documentsRepository.save(doc);
	}

	public void deleteDocument(Long id) {
		Documents doc = documentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));

		s3Service.deleteFile(doc.getDocUrl());
		documentsRepository.delete(doc);
	}

	public Documents get(Long id) {
		return documentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
	}

	public List<Documents> getDocumentsByObject(String objectType, Long objectId) {
		// TODO Auto-generated method stub
		return null;
	}
}
