package com.momentum.releaser.global.config.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Upload {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public String upload(MultipartFile multipartFile) throws IOException {
        // 파일 이름이 중복되지 않게 생성
        String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        // 파일의 사이즈를 ContentLength로 S3에게 알려준다.
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getInputStream().available());

        // S3 API 메서드인 putObject를 이용하여 파일 스트림(Stream)을 열어 S3에 파일을 업로드한다.
        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objectMetadata);

        // getUrl 메서드를 통해서 S3에 업로드된 사진 URL을 가져온다.
        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    public void delete(String url) {
        String fileKey = url.substring(58);
    }
}
