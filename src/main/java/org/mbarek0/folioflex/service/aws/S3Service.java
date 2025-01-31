package org.mbarek0.folioflex.service.aws;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadFile(MultipartFile multipartFile);
}
