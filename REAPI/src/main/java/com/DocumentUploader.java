package com;

import java.io.File;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class DocumentUploader {

    public static void main(String[] args) {

     //   Unirest.setTimeouts(0, 0); // no timeout

        HttpResponse<String> response = Unirest
                .post("http://localhost:8080/api/documents/uploadDocuments/PROPERTY/1")
                .field("files", new File("C:/Users/DELL/Downloads/logs.txt"))
                .field("files", new File("C:/Users/DELL/Downloads/TimBrix.pdf"))
                .field("captions", "Front View")
                .field("captions", "Side View")
                .asString();

        System.out.println("Status: " + response.getStatus());
        System.out.println("Response: " + response.getBody());
    }
}
