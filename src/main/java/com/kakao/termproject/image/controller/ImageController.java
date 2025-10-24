package com.kakao.termproject.image.controller;

import com.kakao.termproject.exception.ErrorResult;
import com.kakao.termproject.image.dto.ImageResponse;
import com.kakao.termproject.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "이미지 업로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

  private final ImageService imageService;

  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "업로드 성공"),
    @ApiResponse(responseCode = "400", description = "업로드에 실패한 경우",
      content = @Content(schema = @Schema(implementation = ErrorResult.class),
        examples = @ExampleObject(
          value = "{\n"
            + "    \"status\": \"BAD_REQUEST\",\n"
            + "    \"message\": \"파일 업로드에 실패했습니다.\"\n"
            + "}"
        ))),
    @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않는 경우",
      content = @Content(schema = @Schema(implementation = ErrorResult.class),
        examples = @ExampleObject(
          value = "{\n"
            + "    \"status\": \"NOT_FOUND\",\n"
            + "    \"message\": \"게시글이 존재하지 않습니다.\"\n"
            + "}"
        )))
  })
  @Operation(summary = "업로드", description = "게시글에 사진을 업로드하는 기능입니다. 사진 한 장의 크기는 최대 10MB를 넘을 수 없으며, jpg, jpeg, png, webp 확장자만 가능합니다. key는 image로 하여 form-data로 업로드해주시면 됩니다.")
  @PostMapping("/{postId}")
  public ResponseEntity<ImageResponse> uploadImages(
    @PathVariable Long postId,
    @RequestPart(value = "image") List<MultipartFile> multipartFiles) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(imageService.upload(postId, multipartFiles));
  }

  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공"),
    @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않는 경우",
      content = @Content(schema = @Schema(implementation = ErrorResult.class),
        examples = @ExampleObject(
          value = "{\n"
            + "    \"status\": \"NOT_FOUND\",\n"
            + "    \"message\": \"게시글이 존재하지 않습니다.\"\n"
            + "}"
        )))
  })
  @Operation(summary = "조회", description = "게시글 사진을 조회하는 기능입니다. 각 사진이 저장된 url 리스트를 반환합니다.")
  @GetMapping("/{postId}")
  public ResponseEntity<List<String>> getImages(@PathVariable Long postId) {
    return ResponseEntity.ok(imageService.getImages(postId));
  }

  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "삭제 성공"),
    @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않는 경우",
      content = @Content(schema = @Schema(implementation = ErrorResult.class),
        examples = @ExampleObject(
          value = "{\n"
            + "    \"status\": \"NOT_FOUND\",\n"
            + "    \"message\": \"게시글이 존재하지 않습니다.\"\n"
            + "}"
        )))
  })
  @Operation(summary = "삭제", description = "게시글 사진을 삭제하는 api입니다. 해당되는 게시글의 사진을 전부 삭제합니다.")
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deleteImages(@PathVariable Long postId) {
    imageService.delete(postId);
    return ResponseEntity.noContent().build();
  }
}
