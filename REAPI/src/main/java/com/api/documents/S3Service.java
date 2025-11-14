package com.api.documents;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.InvalidObjectStateException;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class S3Service {

	private final S3Client s3Client;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	public S3Service(S3Client s3Client) {
		this.s3Client = s3Client;
	}

	public String uploadFile(MultipartFile file, String folderName) throws IOException {
		int randomNum = (int) (Math.random() * 900000) + 100000; // to avoid duplicate filenames
		String fileName = randomNum + "_" + file.getOriginalFilename();

		String key = folderName + "/" + fileName; // <-- add folder name here

		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key)
				.acl(ObjectCannedACL.PUBLIC_READ).build();

		s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

		return key; // returns full path: folderName/12345_filename.jpg
	}

	// Download file
	public byte[] downloadFile(String key) throws NoSuchKeyException, InvalidObjectStateException, S3Exception,
			AwsServiceException, SdkClientException, IOException {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

		return s3Client.getObject(getObjectRequest).readAllBytes();
	}

	// List files
	public List<String> listFiles() {
		ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucketName).build();

		return s3Client.listObjects(listObjectsRequest).contents().stream().map(S3Object::key)
				.collect(Collectors.toList());
	}

	// Delete file
	public void deleteFile(String key) {
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
		s3Client.deleteObject(deleteObjectRequest);
	}
}
