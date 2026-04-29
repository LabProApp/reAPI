package com.api.documents;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for interacting with AWS S3 using the AWS SDK v2.
 *
 * <p>Provides three core operations:
 * <ul>
 *   <li>{@link #uploadFile(MultipartFile, String)} — uploads a multipart file
 *       to S3 under a UUID-prefixed key within an optional folder.</li>
 *   <li>{@link #generatePresignedUrl(String)} — generates a 60-minute
 *       presigned GET URL so clients can download a private object without
 *       requiring AWS credentials.</li>
 *   <li>{@link #deleteFile(String)} — deletes an S3 object by key.</li>
 * </ul>
 *
 * <p>The target S3 bucket and region are read from {@code application.properties}
 * via the {@code aws.s3.bucket-name} and {@code aws.s3.region} keys. The
 * {@link S3Client} and {@link S3Presigner} beans are configured in
 * {@link S3Config}.
 */
@Slf4j
@Service
public class S3Service {

	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	@Value("${aws.s3.region}")
	private String region;
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	/**
	 * Constructs the service with the required AWS SDK clients.
	 *
	 * @param s3Client    the AWS SDK v2 S3 client used for upload and delete
	 * @param s3Presigner the AWS SDK v2 S3 presigner used for generating
	 *                    time-limited download URLs
	 */
	public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	/**
	 * Uploads a multipart file to the configured S3 bucket.
	 *
	 * <p>The S3 object key is composed as
	 * {@code <folderName>/<UUID>_<sanitisedFilename>}. Special characters in
	 * the original filename are replaced with underscores. If {@code folderName}
	 * is {@code null} or empty the key contains no folder prefix.
	 *
	 * @param file       the multipart file to upload; must not be {@code null}
	 *                   or empty
	 * @param folderName the S3 "folder" (key prefix) to place the file under,
	 *                   e.g. {@code "PROPERTY"} or {@code "LOAN_DOCUMENT"};
	 *                   leading and trailing slashes are stripped automatically
	 * @return the S3 object key of the uploaded file
	 * @throws IOException              if reading the file's input stream fails
	 * @throws IllegalArgumentException if {@code file} is {@code null} or empty
	 */
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

	/**
	 * Deletes the S3 object identified by the given key from the configured
	 * bucket.
	 *
	 * @param key the S3 object key to delete; must not be {@code null} or blank
	 * @throws IllegalArgumentException if {@code key} is {@code null} or blank
	 */
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

	/**
	 * Generates a presigned GET URL for the S3 object identified by the given
	 * key. The URL is valid for 60 minutes from the time of generation.
	 *
	 * @param key the S3 object key; must not be {@code null} or blank
	 * @return the presigned download URL as a string
	 * @throws IllegalArgumentException if {@code key} is {@code null} or blank
	 */
	public String generatePresignedUrl(String key) {
		if (key == null || key.isBlank()) {
			log.warn("generatePresignedUrl - Rejected: S3 key is null or blank");
			throw new IllegalArgumentException("S3 key is required to generate presigned URL");
		}
		log.debug("generatePresignedUrl - Generating presigned URL for key={}", key);
		GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(key).build();
		String url = s3Presigner.presignGetObject(p -> p.signatureDuration(Duration.ofMinutes(60)).getObjectRequest(request))
				.url().toString();
		log.debug("generatePresignedUrl - Presigned URL generated for key={}", key);
		return url;
	}
}
