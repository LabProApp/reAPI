package com.api.documents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api.enums.MasterEnums;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class DocumentsService {

	private final DocumentRepository documentsRepository;
	private final S3Service s3Service;
	private final ModelMapper mapper;

	public DocumentsService(DocumentRepository documentsRepository, S3Service s3Service, ModelMapper mapper) {
		this.documentsRepository = documentsRepository;
		this.s3Service = s3Service;
		this.mapper = mapper;
	}

	// Upload multiple documents
	public List<DocumentDto> uploadDocuments(String objectType, Long objectId, List<MultipartFile> files,
			List<String> captions) throws IOException {

		List<DocumentDto> dtoList = new ArrayList<>();

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String caption = (captions != null && captions.size() > i) ? captions.get(i) : null;
			String contentType = file.getContentType();
			String docType = DocTypeDetector.detect(contentType);

			DocumentDto docdto = uploadDocument(file, docType, caption, objectType, objectId);
			dtoList.add(docdto);
		}

		return dtoList;
	}

	// Upload single document
	public DocumentDto uploadDocument(MultipartFile file, String docType, String caption, String objectType,
			Long objectId) throws IOException {

		String originalFilename = file.getOriginalFilename();
		String sanitizedFilename = (originalFilename != null) ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "")
				: "file";

		String s3Url = s3Service.uploadFile(file, objectType.toUpperCase());

		Documents doc = new Documents();
		doc.setDocUrl(s3Url);
		doc.setFilename(sanitizedFilename);
		doc.setDocType(docType);
		doc.setCaption(caption);
		doc.setObjectType(objectType);
		doc.setObjectId(objectId);

		Documents savedDoc = documentsRepository.save(doc);

		return mapper.map(savedDoc, DocumentDto.class);
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

	public DocumentDto updateDocumentStatus(Long documentId, MasterEnums.DocumentStatus status, String rejectionReason,
			String comments) {

		Documents document = documentsRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

		document.setDocumentStatus(status);
		document.setComments(comments);

		if (status == MasterEnums.DocumentStatus.REJECTED) {
			document.setRejectionReason(rejectionReason);
		} else {
			document.setRejectionReason(null);
		}

		Documents savedDocument = documentsRepository.save(document);
		return mapper.map(savedDocument, DocumentDto.class);

	}

	// Get all documents by objectType and objectId
	public List<DocumentDto> getDocumentsByObject(String objectType, Long objectId) {
		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectId(objectType, objectId);

		return docs.stream().map(doc -> mapper.map(doc, DocumentDto.class)).collect(Collectors.toList());
	}
	// Get all documents by objectType and objectId
	public List<DocumentminDto> getminDocumentsByObject(String objectType, Long objectId) {
		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectId(objectType, objectId);

		return docs.stream().map(doc -> mapper.map(doc, DocumentminDto.class)).collect(Collectors.toList());
	}
}
