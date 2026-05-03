package com.api.documents;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class S3Service {

	private static final Logger log = LoggerFactory.getLogger(S3Service.class);

	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	@Value("${aws.s3.region}")
	private String region;
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	
	public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	
	public String uploadFile(MultipartFile file, String folderName) throws IOException {
		if (file == null || file.isEmpty()) {
			log.warn("uploadFile - Rejected: file is null or empty");
			throw new IllegalArgumentException("Upload file is required and cannot be empty");
		}

		String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
		originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

		String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

		String normalizedFolder = folderName != null ? folderName.replaceAll("^/|/$", "") : "";
		String key = (normalizedFolder.isEmpty() ? "" : normalizedFolder + "/") + fileName;

		log.info("uploadFile - Uploading file to S3 bucket={}, key={}, size={}bytes",
				bucketName, key, file.getSize());

		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key)
				.contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
				.build();

		s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		log.info("uploadFile - File uploaded successfully to S3 key={}", key);
		return key;
	}

	
	public void deleteFile(String key) {
		if (key == null || key.isBlank()) {
			log.warn("deleteFile - Rejected: S3 key is null or blank");
			throw new IllegalArgumentException("S3 key must be provided for delete operation");
		}
		log.info("deleteFile - Deleting S3 object bucket={}, key={}", bucketName, key);
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
		s3Client.deleteObject(deleteObjectRequest);
		log.info("deleteFile - S3 object deleted key={}", key);
	}

	
	public String generatePresignedUrl(String key) {
		if (key == null || key.isBlank()) {
			log.warn("generatePresignedUrl - Rejected: S3 key is null or blank");
			throw new IllegalArgumentException("S3 key is required to generate presigned URL");
		}
		log.debug("generatePresignedUrl - Generating presigned URL for key={}", key);
		GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(key).build();
		String url = s3Presigner.presignGetObject(p -> p.signatureDuration(Duration.ofMinutes(15)).getObjectRequest(request))
				.url().toString();
		log.debug("generatePresignedUrl - Presigned URL generated for key={}", key);
		return url;
	}
}
