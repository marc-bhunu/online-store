package com.marcuswhocodes.productservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    String uploadFile(MultipartFile file) throws IOException;
    String getPresignedUrl(String fileName);
}
