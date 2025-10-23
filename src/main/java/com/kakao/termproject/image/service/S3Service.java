package com.kakao.termproject.image.service;

import com.kakao.termproject.exception.custom.BadFormatException;
import com.kakao.termproject.exception.custom.FailedToUploadException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

  private final S3Client s3Client;
  private final String bucketName;

  private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");
  private static final String FILE_PATH = "images/";
  private static final Long MAX_FILE_SIZE = 10 * 1024 * 1024L;

  public S3Service(S3Client s3Client, @Value("${spring.cloud.aws.s3.bucket}") String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  public List<String> upload(List<MultipartFile> files) {
    List<String> results = new ArrayList<>();

    files.forEach(file -> {
      if (file.getSize() > MAX_FILE_SIZE) {
        throw new FailedToUploadException("파일 크기는 최대 10MB를 넘길 수 없습니다.");
      }

      String fileName = createUniqueName(file.getOriginalFilename());

      try {
        s3Client.putObject(
          PutObjectRequest.builder()
            .bucket(bucketName)
            .key(FILE_PATH + fileName)
            .contentType(file.getContentType())
            .build(),
          RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
      } catch (IOException e) {
        throw new FailedToUploadException("파일 업로드에 실패했습니다.");
      } catch (SdkException e) {
        throw new FailedToUploadException("S3 업로드 중 오류가 발생했습니다.");
      }

      results.add(fileName);
    });

    return results;
  }

  private String createUniqueName(String fileName) {
    return UUID.randomUUID().toString().concat(getExtension(fileName));
  }

  private String getExtension(String fileName) {
    String ext = fileName.substring(fileName.lastIndexOf("."));
    if (fileName.lastIndexOf(".") == -1 || !ALLOWED_EXTENSIONS.contains(ext)) {
      throw new BadFormatException();
    }

    return fileName.substring(fileName.lastIndexOf("."));
  }

  public List<String> getImages(List<String> fileNames) {
    List<String> results = new ArrayList<>();

    fileNames.forEach(fileName -> {
      String result = s3Client.utilities()
        .getUrl(url -> url.bucket(bucketName).key(FILE_PATH + fileName)).toString();

      results.add(result);
    });

    return results;
  }

  public void delete(List<String> files) {
    files.forEach(file -> {
      s3Client.deleteObject(
        DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(FILE_PATH + file)
          .build()
      );
    });
  }
}
