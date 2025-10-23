package com.kakao.termproject.image.service;

import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.image.domain.Image;
import com.kakao.termproject.image.dto.ImageResponse;
import com.kakao.termproject.image.repository.ImageRepository;
import com.kakao.termproject.post.domain.Post;
import com.kakao.termproject.post.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final S3Service s3Service;
  private final ImageRepository imageRepository;
  private final PostRepository postRepository;

  @Transactional
  public ImageResponse upload(Long postId, List<MultipartFile> files) {
    List<String> images = s3Service.upload(files);

    Post post = postRepository.findById(postId)
      .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다."));

    images.forEach(image -> {
      imageRepository.save(new Image(image, post));
    });

    return new ImageResponse(postId, images);
  }

  @Transactional(readOnly = true)
  public List<String> getImages(Long postId) {
    Post post = postRepository.findById(postId)
      .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다."));

    List<String> images = imageRepository.findAllByPost(post)
      .stream()
      .map(Image::getName)
      .toList();

    return s3Service.getImages(images);
  }

  @Transactional
  public void delete(Long postId) {
    Post post = postRepository.findById(postId)
      .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다."));

    List<String> images = imageRepository.findAllByPost(post)
      .stream()
      .map(Image::getName)
      .toList();

    s3Service.delete(images);
    imageRepository.deleteByPost(post);
  }
}
