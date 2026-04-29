package com.api.documents;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Spring configuration class that creates and exposes AWS S3 beans required
 * by {@link S3Service}.
 *
 * <p>AWS credentials and the target region are read from
 * {@code application.properties} using the following keys:
 * <ul>
 *   <li>{@code aws.access-key} — AWS access key ID</li>
 *   <li>{@code aws.secret-key} — AWS secret access key</li>
 *   <li>{@code aws.region}     — AWS region (e.g. {@code "ap-south-1"})</li>
 * </ul>
 *
 * <p>Both beans use {@link StaticCredentialsProvider} backed by
 * {@link AwsBasicCredentials} constructed from the injected properties.
 */
@Configuration
public class S3Config {

	@Value("${aws.access-key}")
	private String accessKey;

	@Value("${aws.secret-key}")
	private String secretKey;

	@Value("${aws.region}")
	private String region;

	/**
	 * Creates an {@link S3Client} bean configured for the specified AWS region
	 * and static credentials.
	 *
	 * <p>Used by {@link S3Service} for synchronous upload and delete
	 * operations.
	 *
	 * @return a fully configured {@link S3Client} instance
	 */
	@Bean
	public S3Client s3Client() {
		return S3Client.builder().region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.build();
	}

	/**
	 * Creates an {@link S3Presigner} bean configured for the specified AWS
	 * region and static credentials.
	 *
	 * <p>Used by {@link S3Service} to generate time-limited presigned GET URLs
	 * for private S3 objects.
	 *
	 * @return a fully configured {@link S3Presigner} instance
	 */
	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder().region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.build();
	}
}
