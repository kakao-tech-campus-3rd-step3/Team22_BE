package com.kakao.termproject.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kakao.termproject.exception.custom.OwnerMismatchException;
import com.kakao.termproject.history.dto.HistoryResponse;
import com.kakao.termproject.history.repository.HistoryRepository;
import com.kakao.termproject.history.service.HistoryService;
import com.kakao.termproject.map.dto.Coordinate;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.repository.MemberRepository;
import com.kakao.termproject.walk.dto.WalkData;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class historyServiceTest {

  @Autowired
  private HistoryService historyService;
  @Autowired
  private HistoryRepository historyRepository;
  @Autowired
  private MemberRepository memberRepository;

  private WalkData mockData;
  private Member mockMember;

  @BeforeEach
  void setUp() {
    mockData = new WalkData(
      2010.39,
      1800,
      createCoordinates()
    );

    mockMember = memberRepository.save(
      new Member(
        "test1234@test.com",
        "test1234",
        "1234"
      )
    );
  }

  @AfterEach
  void tearDown() {
    historyRepository.deleteAll();
    memberRepository.deleteAll();
  }

  @Test
  void 기록_저장_테스트() {
    HistoryResponse response = historyService.saveHistory(mockData, mockMember);

    assertThat(response.id()).isNotNull();
    assertThat(response.createdAt()).isNotNull();
    assertThat(response.walk()).isNotNull();
  }

  @Test
  void 기록_조회_테스트() {
    HistoryResponse history = historyService.saveHistory(mockData, mockMember);

    HistoryResponse response = historyService.getHistory(history.id(), mockMember);

    assertThat(response.id()).isNotNull();
    assertThat(response.createdAt()).isNotNull();
    assertThat(response.walk()).isNotNull();
  }

  @Test
  void 기록_전체_조회_테스트() {
    for (int i = 0; i < 5; i++) {
      historyService.saveHistory(mockData, mockMember);
    }

    List<HistoryResponse> responses = historyService.getAllHistories(mockMember);

    assertThat(responses.size()).isGreaterThan(4);
    assertThat(responses.get(0).id()).isNotNull();
    assertThat(responses.get(1).id()).isNotNull();
    assertThat(responses.get(2).id()).isNotNull();
    assertThat(responses.get(3).id()).isNotNull();
  }

  @Test
  void 조회하는_기록이_로그인_중인_멤버와_일치하지_않는_경우_오류_반환() {
    HistoryResponse history = historyService.saveHistory(mockData, mockMember);
    Member anotherMember = memberRepository.save(
      new Member(
        "another@gmail.com",
        "another",
        "1234"
      )
    );

    OwnerMismatchException ex = assertThrows(
      OwnerMismatchException.class,
      () -> historyService.getHistory(history.id(), anotherMember)
    );

    assertEquals("접근 권한이 없습니다.", ex.getMessage());
  }


  private Coordinate[] createCoordinates() {
    return new Coordinate[]{
      new Coordinate(36.369887, 127.340291),
      new Coordinate(36.369887, 127.340313),
      new Coordinate(36.369887, 127.340335)
    };
  }
}
