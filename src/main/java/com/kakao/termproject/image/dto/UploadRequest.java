package com.kakao.termproject.image.dto;

import org.springframework.web.multipart.MultipartFile;

public record UploadRequest(String fileName, MultipartFile file) {

}
