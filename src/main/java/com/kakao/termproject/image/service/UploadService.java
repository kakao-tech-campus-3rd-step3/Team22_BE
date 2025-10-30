package com.kakao.termproject.image.service;

import com.kakao.termproject.exception.custom.BadFormatException;
import com.kakao.termproject.exception.custom.FailedToUploadException;
import com.kakao.termproject.image.properties.ImageProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class UploadService {

  private final S3Client s3Client;
  private final ImageProperties imageProperties;

  public List<String> upload(List<MultipartFile> files) {
    List<String> results = new ArrayList<>();

    files.forEach(file -> {
      if (file.getSize() > imageProperties.maxFileSize()) {
        throw new FailedToUploadException("파일 크기는 최대 10MB를 넘길 수 없습니다.");
      }

      String fileName = createUniqueName(file.getOriginalFilename());

      try {
        s3Client.putObject(
          PutObjectRequest.builder()
            .bucket(imageProperties.bucketName())
            .key(imageProperties.filePath() + fileName)
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
    String ext = StringUtils.getFilenameExtension(fileName);
    if (ext == null || !imageProperties.allowedExtensions().contains("." + ext)) {
      throw new BadFormatException();
    }

    return "." + ext;
  }

  public List<String> getImages(List<String> fileNames) {
    return fileNames.stream()
      .map(fileName -> s3Client.utilities()
        .getUrl(url -> url.bucket(imageProperties.bucketName())
          .key(imageProperties.filePath() + fileName)).toString())
      .toList();
  }

  public void delete(List<String> files) {
    List<ObjectIdentifier> objects = files.stream()
      .map(file -> ObjectIdentifier.builder()
        .key(imageProperties.filePath() + file)
        .build())
      .toList();

    DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
      .bucket(imageProperties.bucketName())
      .delete(Delete.builder()
        .objects(objects)
        .build())
      .build();

    s3Client.deleteObjects(deleteObjectsRequest);
  }
}
