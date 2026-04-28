package com.api.documents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api.enums.MasterEnums;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		log.info("uploadDocuments - Uploading {} file(s) for objectType={}, objectId={}", files.size(), objectType, objectId);
		List<DocumentDto> dtoList = new ArrayList<>();

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String caption = (captions != null && captions.size() > i) ? captions.get(i) : null;
			String contentType = file.getContentType();
			String docType = DocTypeDetector.detect(contentType);
			log.debug("uploadDocuments - Processing file[{}]: name={}, contentType={}, caption={}",
					i, file.getOriginalFilename(), contentType, caption);
			DocumentDto docdto = uploadDocument(file, docType, caption, objectType, objectId);
			dtoList.add(docdto);
		}

		log.info("uploadDocuments - Successfully uploaded {} document(s) for objectType={}, objectId={}",
				dtoList.size(), objectType, objectId);
		return dtoList;
	}

	// Upload single document
	public DocumentDto uploadDocument(MultipartFile file, String docType, String caption, String objectType,
			Long objectId) throws IOException {
		log.debug("uploadDocument - Uploading file: {}, docType={}", file.getOriginalFilename(), docType);
		String originalFilename = file.getOriginalFilename();
		String sanitizedFilename = (originalFilename != null) ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "")
				: "file";

		String key = s3Service.uploadFile(file, objectType.toUpperCase());
		log.debug("uploadDocument - File uploaded to S3 with key={}", key);

		Documents doc = new Documents();
		doc.setS3key(key);
		doc.setFilename(sanitizedFilename);
		doc.setDocType(docType);
		doc.setCaption(caption);
		doc.setObjectType(objectType);
		doc.setObjectId(objectId);

		Documents savedDoc = documentsRepository.save(doc);
		log.info("uploadDocument - Document saved with id={}, key={}", savedDoc.getId(), key);
		return mapper.map(savedDoc, DocumentDto.class);
	}

	// Delete a document
	public void deleteDocument(Long id) {
		log.info("deleteDocument - Deleting document id={}", id);
		Documents doc = documentsRepository.findById(id).orElseThrow(() -> {
			log.error("deleteDocument - Document not found for id={}", id);
			return new RuntimeException("Document not found");
		});
		documentsRepository.delete(doc);
		log.info("deleteDocument - Document id={} deleted (s3key={})", id, doc.getS3key());
	}

	// Get a single document
	public DocumentDto get(Long id) {
		Documents doc = documentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));

		return mapper.map(doc, DocumentDto.class);
	}

	public String generatePresignedUrl(String key) {
		return s3Service.generatePresignedUrl(key);

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
		log.info("getDocumentsByObject - Fetching documents for objectType={}, objectId={}", objectType, objectId);
		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectId(objectType, objectId);
		log.debug("getDocumentsByObject - Found {} documents for objectType={}, objectId={}", docs.size(), objectType, objectId);

		return docs.stream().map(doc -> {
			DocumentDto dto = mapper.map(doc, DocumentDto.class);
			if (doc.getS3key() != null && !doc.getS3key().isBlank()) {
				try {
					dto.setDocUrl(s3Service.generatePresignedUrl(doc.getS3key()));
				} catch (Exception e) {
					log.error("getDocumentsByObject - Failed to generate presigned URL for key={}: {}",
							doc.getS3key(), e.getMessage());
				}
			}
			return dto;
		}).collect(Collectors.toList());
	}

	// ─── Loan document upload ────────────────────────────────────────────────

	public List<DocumentDto> uploadLoanDocuments(Long objectId, List<MultipartFile> files,
			List<String> titles, List<String> captions) throws IOException {
		log.info("uploadLoanDocuments - Uploading {} file(s) for objectId={}", files.size(), objectId);
		return uploadDocumentsTyped("LOAN_DOCUMENT", objectId, files, titles, captions);
	}

	// ─── Legal document upload ────────────────────────────────────────────────

	public List<DocumentDto> uploadLegalDocuments(Long objectId, List<MultipartFile> files,
			List<String> titles, List<String> captions) throws IOException {
		log.info("uploadLegalDocuments - Uploading {} file(s) for objectId={}", files.size(), objectId);
		return uploadDocumentsTyped("LEGAL_DOCUMENT", objectId, files, titles, captions);
	}

	private List<DocumentDto> uploadDocumentsTyped(String objectType, Long objectId,
			List<MultipartFile> files, List<String> titles, List<String> captions) throws IOException {
		List<DocumentDto> result = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String title = (titles != null && titles.size() > i) ? titles.get(i) : null;
			String caption = (captions != null && captions.size() > i) ? captions.get(i) : null;
			String docType = DocTypeDetector.detect(file.getContentType());
			log.debug("uploadDocumentsTyped - file[{}]: name={}, title={}, objectType={}", i, file.getOriginalFilename(), title, objectType);

			String sanitizedFilename = file.getOriginalFilename() != null
					? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "") : "file";
			String key = s3Service.uploadFile(file, objectType);

			Documents doc = new Documents();
			doc.setS3key(key);
			doc.setFilename(sanitizedFilename);
			doc.setTitle(title);
			doc.setDocType(docType);
			doc.setCaption(caption);
			doc.setObjectType(objectType);
			doc.setObjectId(objectId);
			doc.setDocumentStatus(MasterEnums.DocumentStatus.NOT_VERIFIED);

			Documents saved = documentsRepository.save(doc);
			log.info("uploadDocumentsTyped - Saved doc id={}, title={}, key={}", saved.getId(), title, key);
			result.add(mapper.map(saved, DocumentDto.class));
		}
		return result;
	}

	// ─── Status update ────────────────────────────────────────────────────────

	// ─── Fetch by status ─────────────────────────────────────────────────────

	public List<DocumentDto> getPendingDocuments() {
		log.info("getPendingDocuments - Fetching all NOT_VERIFIED documents");
		List<Documents> docs = documentsRepository.findByDocumentStatus(MasterEnums.DocumentStatus.NOT_VERIFIED);
		log.info("getPendingDocuments - Found {} NOT_VERIFIED documents", docs.size());
		return docs.stream().map(doc -> {
			DocumentDto dto = mapper.map(doc, DocumentDto.class);
			if (doc.getS3key() != null && !doc.getS3key().isBlank()) {
				try {
					dto.setDocUrl(s3Service.generatePresignedUrl(doc.getS3key()));
				} catch (Exception e) {
					log.error("getPendingDocuments - Failed to generate presigned URL for key={}: {}", doc.getS3key(), e.getMessage());
				}
			}
			return dto;
		}).collect(Collectors.toList());
	}

	public List<DocumentDto> getDocumentsByObjectAndStatus(String objectType, Long objectId,
			MasterEnums.DocumentStatus status) {
		log.info("getDocumentsByObjectAndStatus - objectType={}, objectId={}, status={}", objectType, objectId, status);
		List<Documents> docs = documentsRepository
				.findByObjectTypeIgnoreCaseAndObjectIdAndDocumentStatus(objectType, objectId, status);
		log.info("getDocumentsByObjectAndStatus - Found {} documents", docs.size());
		return docs.stream().map(doc -> {
			DocumentDto dto = mapper.map(doc, DocumentDto.class);
			if (doc.getS3key() != null && !doc.getS3key().isBlank()) {
				try {
					dto.setDocUrl(s3Service.generatePresignedUrl(doc.getS3key()));
				} catch (Exception e) {
					log.error("getDocumentsByObjectAndStatus - Presigned URL failed for key={}: {}", doc.getS3key(), e.getMessage());
				}
			}
			return dto;
		}).collect(Collectors.toList());
	}

	// Get all documents by objectType and objectId
	public List<DocumentminDto> getminDocumentsByObject(String objectType, Long objectId) {
		if (objectType == null || objectId == null) {
			return Collections.emptyList();
		}

		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectId(objectType, objectId);

		if (docs.isEmpty()) {
			return Collections.emptyList();
		}

		log.debug("getminDocumentsByObject - Found {} documents for objectType={}, objectId={}", docs.size(), objectType, objectId);
		return docs.stream().map(doc -> {
			DocumentminDto dto = mapper.map(doc, DocumentminDto.class);

			if (doc.getS3key() != null && !doc.getS3key().isEmpty()) {
				try {
					dto.setDocUrl(s3Service.generatePresignedUrl(doc.getS3key()));
				} catch (Exception e) {
					log.error("getminDocumentsByObject - Failed to generate presigned URL for key={}: {}",
							doc.getS3key(), e.getMessage());
				}
			}

			return dto;
		}).collect(Collectors.toList());
	}

	
}
