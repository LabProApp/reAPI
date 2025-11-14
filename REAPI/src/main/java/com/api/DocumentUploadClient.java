package com.api;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;

public class DocumentUploadClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/api/documents/uploadDocuments";

    public List<?> uploadDocuments(
            String objectType,
            Long objectId,
            List<File> files,
            List<String> captions
    ) {

        String url = BASE_URL + "/" + objectType + "/" + objectId;

        // Prepare multipart body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Add files
        for (File file : files) {
            body.add("files", new FileSystemResource(file));
        }

        // Add captions
        if (captions != null && !captions.isEmpty()) {
            for (String caption : captions) {
                body.add("captions", caption);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                List.class
        );

        return response.getBody();
    }

    public static void main(String[] args) {

        DocumentUploadClient client = new DocumentUploadClient();

        List<File> files = List.of(
                new File("C:/d/naira 2.png"),
                new File("C:/d/naira drawing.png")
        );

        List<String> captions = List.of("Front View", "Side View");

        List<?> uploaded = client.uploadDocuments("PROPERTY", 101L, files, captions);

        System.out.println("Uploaded Documents Response:");
        System.out.println(uploaded);
    }
}
