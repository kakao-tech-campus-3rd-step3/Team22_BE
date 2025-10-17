package com.kakao.termproject.history.service;

import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.exception.custom.OwnerMismatchException;
import com.kakao.termproject.history.domain.History;
import com.kakao.termproject.history.dto.HistoryResponse;
import com.kakao.termproject.history.repository.HistoryRepository;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.dto.WalkData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryService {

  private final HistoryRepository historyRepository;

  @Transactional
  public HistoryResponse saveHistory(WalkData walk, Member member) {
    History history = historyRepository.save(new History(walk, member));

    return convertToDTO(history);
  }

  @Transactional(readOnly = true)
  public HistoryResponse getHistory(Long id, Member member) {
    History history = historyRepository.findById(id)
      .orElseThrow(() -> new DataNotFoundException("해당 id를 가진 산책 기록이 존재하지 않습니다."));

    if (!history.getMember().getId().equals(member.getId())) {
      throw new OwnerMismatchException("접근 권한이 없습니다.");
    }

    return convertToDTO(history);
  }

  @Transactional(readOnly = true)
  public List<HistoryResponse> getAllHistories(Member member) {
    List<History> histories = historyRepository.findAllByMember(member);

    return histories.stream()
      .map(this::convertToDTO)
      .toList();
  }

  private HistoryResponse convertToDTO(History history) {
    return new HistoryResponse(
      history.getId(),
      history.getCreatedAt(),
      history.getWalk()
    );
  }
}
