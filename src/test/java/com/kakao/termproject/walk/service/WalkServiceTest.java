package com.kakao.termproject.walk.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kakao.termproject.map.dto.Coordinate;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.repository.MemberRepository;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.dto.WalkResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WalkServiceTest {

  @Autowired
  private WalkService walkService;
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

    mockMember = new Member(
      "test1234@test.com",
      "test1234",
      "1234"
    );

    memberRepository.save(mockMember);
  }

  @Test
  void 경로_저장_및_경사도_반환_테스트() {
    WalkSaveResponse response = walkService.saveWalk(mockMember, mockData);

    assertThat(response.walkResponse().id()).isNotNull();

    WalkResponse walkResponse = walkService.getWalk(mockMember);

    assertThat(walkResponse).isNotNull();
    assertThat(walkResponse.maxSlope()).isGreaterThan(-0.0);
    assertThat(walkResponse.avgOfSlope()).isGreaterThan(-0.0);
    assertThat(walkResponse.walkData()).isNotNull();
  }

  private Coordinate[] createCoordinates() {
    return new Coordinate[]{
      new Coordinate(36.369887, 127.340291),
      new Coordinate(36.369887, 127.340313),
      new Coordinate(36.369887, 127.340335),
      new Coordinate(36.369887, 127.340380),
      new Coordinate(36.369887, 127.340491),
      new Coordinate(36.369886, 127.340514),
      new Coordinate(36.369886, 127.340536),
      new Coordinate(36.369886, 127.340558),
      new Coordinate(36.369886, 127.340580),
      new Coordinate(36.369886, 127.340603),
      new Coordinate(36.369886, 127.340625),
      new Coordinate(36.369886, 127.340759),
      new Coordinate(36.369886, 127.340781),
      new Coordinate(36.369886, 127.340826),
      new Coordinate(36.369904, 127.340848),
      new Coordinate(36.369903, 127.340959),
      new Coordinate(36.369903, 127.340982),
      new Coordinate(36.369939, 127.341004),
      new Coordinate(36.369939, 127.341026),
      new Coordinate(36.369939, 127.341049),
      new Coordinate(36.369939, 127.341071),
      new Coordinate(36.369957, 127.341093),
      new Coordinate(36.369957, 127.341160),
      new Coordinate(36.369975, 127.341183),
      new Coordinate(36.369975, 127.341205),
      new Coordinate(36.369975, 127.341227),
      new Coordinate(36.369974, 127.341272),
      new Coordinate(36.369974, 127.341294),
      new Coordinate(36.369974, 127.341316),
      new Coordinate(36.369974, 127.341339),
      new Coordinate(36.369974, 127.341383),
      new Coordinate(36.369992, 127.341405),
      new Coordinate(36.369992, 127.341450),
      new Coordinate(36.369992, 127.341495),
      new Coordinate(36.370010, 127.341517),
      new Coordinate(36.370010, 127.341539),
      new Coordinate(36.370010, 127.341562),
      new Coordinate(36.370028, 127.341584),
      new Coordinate(36.370027, 127.341651),
      new Coordinate(36.370081, 127.341807),
      new Coordinate(36.370081, 127.341829),
      new Coordinate(36.370081, 127.341852),
      new Coordinate(36.370081, 127.341874),
      new Coordinate(36.370099, 127.341896),
      new Coordinate(36.370117, 127.341896),
      new Coordinate(36.370135, 127.341941),
      new Coordinate(36.370153, 127.341941),
      new Coordinate(36.370153, 127.341963),
      new Coordinate(36.370153, 127.341986),
      new Coordinate(36.370153, 127.342008),
      new Coordinate(36.370242, 127.342253),
      new Coordinate(36.370260, 127.342254),
      new Coordinate(36.370260, 127.342276),
      new Coordinate(36.370260, 127.342298),
      new Coordinate(36.370260, 127.342320),
      new Coordinate(36.370278, 127.342320)
    };
  }
}
