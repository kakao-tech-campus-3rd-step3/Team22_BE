package com.kakao.termproject.post.service;

import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.exception.custom.OwnerMismatchException;
import com.kakao.termproject.exception.custom.UserNotFoundException;
import com.kakao.termproject.post.domain.Post;
import com.kakao.termproject.post.dto.PagedQuery;
import com.kakao.termproject.post.dto.PostRequest;
import com.kakao.termproject.post.dto.PostResponse;
import com.kakao.termproject.post.repository.PostRepository;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public PostResponse savePost(PostRequest postRequest, Member member) {
    Post post = postRepository.save(new Post(postRequest.title(), postRequest.content(), member));

    return convertToDTO(post);
  }

  @Transactional(readOnly = true)
  public PostResponse getPost(Long id) {
    Post post = postRepository.findById(id)
      .orElseThrow(() -> new DataNotFoundException("해당 게시글을 찾을 수 없습니다."));

    return convertToDTO(post);
  }

  @Transactional(readOnly = true)
  public Page<PostResponse> getPosts(PagedQuery pagedQuery) {
    Pageable pageable = setPageable(pagedQuery);

    Page<Post> posts = postRepository.findAll(pageable);

    return posts.map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public Page<PostResponse> getPostsByMemberId(Long memberId, PagedQuery pagedQuery) {
    Pageable pageable = setPageable(pagedQuery);

    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new UserNotFoundException("해당하는 유저가 존재하지 않습니다."));

    Page<Post> posts = postRepository.findAllByMember(member, pageable);

    return posts.map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public Page<PostResponse> getMyPosts(Member member, PagedQuery pagedQuery) {
    Pageable pageable = setPageable(pagedQuery);

    Page<Post> posts = postRepository.findAllByMember(member, pageable);

    return posts.map(this::convertToDTO);
  }

  @Transactional
  public PostResponse updatePost(Long id, PostRequest postRequest, Member member) {
    Post post = postRepository.findById(id)
      .orElseThrow(() -> new DataNotFoundException("해당 게시글을 찾을 수 없습니다."));

    if (!post.getMember().getId().equals(member.getId())) {
      throw new OwnerMismatchException("접근 권한이 없습니다.");
    }

    post.updatePost(postRequest.title(), postRequest.content());

    return convertToDTO(post);
  }

  @Transactional
  public void deletePost(Long id, Member member) {
    Post post = postRepository.findById(id)
      .orElseThrow(() -> new DataNotFoundException("해당 게시글을 찾을 수 없습니다."));

    if (!post.getMember().getId().equals(member.getId())) {
      throw new OwnerMismatchException("접근 권한이 없습니다.");
    }

    postRepository.deleteById(id);
  }

  private Pageable setPageable(PagedQuery pagedQuery) {
    return PageRequest.of(
      pagedQuery.page(),
      pagedQuery.size(),
      Sort.by(pagedQuery.direction(), pagedQuery.criteria())
    );
  }

  private PostResponse convertToDTO(Post post) {
    return new PostResponse(
      post.getId(),
      post.getTitle(),
      post.getContent(),
      post.getCreatedAt(),
      post.getUpdatedAt(),
      post.getMember().getId()
    );
  }
}
