package com.kakao.termproject.image.repository;

import com.kakao.termproject.image.domain.Image;
import com.kakao.termproject.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findAllByPost(Post post);

  void deleteByPost(Post post);
}
