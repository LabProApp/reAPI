package com.api.documents;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class S3Service {

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

		int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999);
		String fileName = randomNum + "_" + file.getOriginalFilename();
		String key = folderName + "/" + fileName;

		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key)
				.contentType(file.getContentType()).build();

		s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

		return key; // ONLY key
	}

	public String generatePresignedUrl(String key) {

		GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(key).build();

		return s3Presigner.presignGetObject(p -> p.signatureDuration(Duration.ofMinutes(60)).getObjectRequest(request))
				.url().toString();
	}
}
