package com.api.documents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentsService {

	private final DocumentRepository documentsRepository;
	private final S3Service s3Service;
	private final ModelMapper mapper;

	// Upload multiple documents

	// Upload multiple documents
	public List<DocumentDto> uploadDocuments(String objectType, Long objectId, List<MultipartFile> files,
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

		// Map entities to DTOs
		List<DocumentDto> dtoList = savedDocs.stream().map(doc -> mapper.map(doc, DocumentDto.class))
				.collect(Collectors.toList());

		// Return as ResponseEntity with 200 OK
		return dtoList;
	}

	// Upload single document
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

		Documents savedDoc = documentsRepository.save(doc);

		return savedDoc;
	}

	// Delete a document
	public void deleteDocument(Long id) {
		Documents doc = documentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));

		s3Service.deleteFile(doc.getDocUrl());
		documentsRepository.delete(doc);
	}

	// Get a single document
	public DocumentDto get(Long id) {
		Documents doc = documentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));

		return mapper.map(doc, DocumentDto.class);
	}

	// Get all documents by objectType and objectId
	public List<DocumentDto> getDocumentsByObject(String objectType, Long objectId) {
		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectId(objectType, objectId);

		return docs.stream().map(doc -> mapper.map(doc, DocumentDto.class)).collect(Collectors.toList());
	}
}
