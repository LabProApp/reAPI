package com.api.documents;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

	// Presigned URL TTL on S3, and a slightly shorter cache TTL so we never return
	// an about-to-expire URL.
	private static final Duration PRESIGN_TTL = Duration.ofMinutes(15);
	private static final long CACHE_TTL_MS   = 10 * 60 * 1000L; // 10 minutes

	private record CachedUrl(String url, long expiresAtMs) { }
	private final ConcurrentHashMap<String, CachedUrl> urlCache = new ConcurrentHashMap<>();

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
		urlCache.remove(key); // invalidate any cached presigned URL
		log.info("deleteFile - S3 object deleted key={}", key);
	}

	public String generatePresignedUrl(String key) {
		if (key == null || key.isBlank()) {
			log.warn("generatePresignedUrl - Rejected: S3 key is null or blank");
			throw new IllegalArgumentException("S3 key is required to generate presigned URL");
		}

		long now = System.currentTimeMillis();
		CachedUrl cached = urlCache.get(key);
		if (cached != null && cached.expiresAtMs() > now) {
			return cached.url();
		}

		GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(key).build();
		String url = s3Presigner.presignGetObject(p -> p.signatureDuration(PRESIGN_TTL).getObjectRequest(request))
				.url().toString();
		urlCache.put(key, new CachedUrl(url, now + CACHE_TTL_MS));

		// Opportunistic cleanup: if the cache balloons, prune expired entries.
		if (urlCache.size() > 5000) {
			urlCache.entrySet().removeIf(e -> e.getValue().expiresAtMs() <= now);
		}
		return url;
	}
}
