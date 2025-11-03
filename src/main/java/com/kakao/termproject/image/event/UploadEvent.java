package com.kakao.termproject.image.event;

import com.kakao.termproject.image.dto.UploadRequest;
import java.util.List;

public record UploadEvent(List<UploadRequest> uploadRequests) {

}
