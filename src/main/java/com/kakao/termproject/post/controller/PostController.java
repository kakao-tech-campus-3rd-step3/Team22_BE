package com.kakao.termproject.post.controller;

import com.kakao.termproject.post.dto.PostRequest;
import com.kakao.termproject.post.dto.PostResponse;
import com.kakao.termproject.post.service.PostService;
import com.kakao.termproject.user.domain.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게시글 CRUD")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<PostResponse> savePost(
    @AuthenticationPrincipal Member member,
    @RequestBody @Valid PostRequest postRequest
  ) {
    log.info("Post request");
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(postService.savePost(postRequest, member));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
    log.info("get post id: {}", id);
    return ResponseEntity.ok(postService.getPost(id));
  }

  @GetMapping
  public ResponseEntity<Page<PostResponse>> getPosts(
    @RequestParam(required = false) Long memberId,
    Pageable pageable
  ) {
    log.info("get posts");

    if (memberId != null) {
      log.info("memberId: {}", memberId);
      return ResponseEntity.ok(postService.getPostsByMemberId(memberId, pageable));
    }

    return ResponseEntity.ok(postService.getPosts(pageable));
  }

  @GetMapping("/my")
  public ResponseEntity<Page<PostResponse>> getMyPosts(
    @AuthenticationPrincipal Member member,
    Pageable pageable
  ) {
    log.info("get my posts");
    return ResponseEntity.ok(postService.getMyPosts(member, pageable));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PostResponse> updatePost(
    @AuthenticationPrincipal Member member,
    @PathVariable Long id,
    @RequestBody @Valid PostRequest postRequest) {
    log.info("update post id: {}", id);
    return ResponseEntity.ok(postService.updatePost(id, postRequest, member));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePost(
    @AuthenticationPrincipal Member member,
    @PathVariable Long id) {
    log.info("delete post id: {}", id);
    postService.deletePost(id, member);
    return ResponseEntity.noContent().build();
  }
}
