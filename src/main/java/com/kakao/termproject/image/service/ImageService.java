package com.kakao.termproject.image.service;

import com.kakao.termproject.exception.custom.BadFormatException;
import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.exception.custom.FailedToUploadException;
import com.kakao.termproject.image.domain.Image;
import com.kakao.termproject.image.dto.ImageResponse;
import com.kakao.termproject.image.dto.UploadRequest;
import com.kakao.termproject.image.event.DeleteEvent;
import com.kakao.termproject.image.event.UploadEvent;
import com.kakao.termproject.image.properties.ImageProperties;
import com.kakao.termproject.image.repository.ImageRepository;
import com.kakao.termproject.post.domain.Post;
import com.kakao.termproject.post.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository imageRepository;
  private final PostRepository postRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final UploadService uploadService;
  private final ImageProperties imageProperties;

  @Transactional
  public ImageResponse upload(Long postId, List<MultipartFile> files) {
    Post post = postRepository.findById(postId)
      .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다."));

    List<UploadRequest> images = new ArrayList<>();

    files.forEach(file -> {
      if (file.getSize() > imageProperties.maxFileSize()) {
        throw new FailedToUploadException("파일 크기는 최대 10MB를 넘길 수 없습니다.");
      }

      String newFileName = createUniqueName(file.getOriginalFilename());
      images.add(new UploadRequest(newFileName, file));
    });

    List<Image> imageEntities = images.stream()
      .map(image -> new Image(image.fileName(), post))
      .toList();

    imageRepository.saveAll(imageEntities);

    eventPublisher.publishEvent(new UploadEvent(images));

    return new ImageResponse(postId);
  }

  @Transactional(readOnly = true)
  public List<String> getImages(Long postId) {
    Post post = postRepository.findById(postId)
      .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다."));

    List<String> images = imageRepository.findAllByPost(post)
      .stream()
      .map(Image::getName)
      .toList();

    return uploadService.getImages(images);
  }

  @Transactional
  public void delete(Long postId) {
    Post post = postRepository.findById(postId)
      .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다."));

    List<String> images = imageRepository.findAllByPost(post)
      .stream()
      .map(Image::getName)
      .toList();

    eventPublisher.publishEvent(new DeleteEvent(images));

    imageRepository.deleteByPost(post);
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
}
