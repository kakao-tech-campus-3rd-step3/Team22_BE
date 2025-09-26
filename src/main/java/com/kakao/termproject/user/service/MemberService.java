package com.kakao.termproject.user.service;

import com.kakao.termproject.exception.custom.UserNotFoundException;
import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.pet.dto.PetCreateRequest;
import com.kakao.termproject.pet.service.PetService;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final PetService petService;

  @Transactional
  public void setPet(Long userId, PetCreateRequest request){
    Pet pet = petService.create(request);
    Member member = memberRepository.findById(userId).
        orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

    member.assignPet(pet);
  }

  @Override
  public UserDetails loadUserByUsername(String username){
    return memberRepository.findUserByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
