package org.mbarek0.folioflex.service.aws.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import com.amazonaws.services.s3.model.ObjectMetadata;


import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    private AmazonS3 s3Client;

    @PostConstruct
    private void initialize() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.EU_NORTH_1)
                .build();
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        try {
            // Generate unique file name
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            // Create date-based folder structure
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String folderPath = dateFormatter.format(LocalDate.now());
            String filePath = folderPath + "/" + fileName;

            // Set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            // Upload to S3
            s3Client.putObject(bucketName, filePath, multipartFile.getInputStream(), metadata);

            // Return full S3 URL
            return s3Client.getUrl(bucketName, filePath).toString();

        } catch (IOException e) {
            log.error("File upload error: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }
}