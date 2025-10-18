package com.kakao.termproject.history.service;

import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.exception.custom.OwnerMismatchException;
import com.kakao.termproject.history.domain.History;
import com.kakao.termproject.history.dto.HistoryResponse;
import com.kakao.termproject.history.dto.PagedQuery;
import com.kakao.termproject.history.repository.HistoryRepository;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.dto.WalkData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    if (!history.isOwner(member)) {
      throw new OwnerMismatchException("접근 권한이 없습니다.");
    }

    return convertToDTO(history);
  }

  @Transactional(readOnly = true)
  public Page<HistoryResponse> getAllHistories(Member member, PagedQuery pagedQuery) {
    Pageable pageable = PageRequest.of(
      pagedQuery.page(),
      pagedQuery.size(),
      Sort.by(pagedQuery.direction(), pagedQuery.criteria())
    );

    Page<History> histories = historyRepository.findAllByMember(member, pageable);

    return histories.map(this::convertToDTO);
  }

  private HistoryResponse convertToDTO(History history) {
    return new HistoryResponse(
      history.getId(),
      history.getCreatedAt(),
      history.getWalk()
    );
  }
}
