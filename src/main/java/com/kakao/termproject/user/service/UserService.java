package com.kakao.termproject.user.service;

import com.kakao.termproject.exception.custom.EmailDuplicationException;
import com.kakao.termproject.exception.custom.InvalidPasswordException;
import com.kakao.termproject.exception.custom.UserNotFoundException;
import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.pet.dto.PetCreateRequest;
import com.kakao.termproject.pet.service.PetService;
import com.kakao.termproject.user.domain.User;
import com.kakao.termproject.user.dto.LoginRequest;
import com.kakao.termproject.user.dto.RegisterRequest;
import com.kakao.termproject.user.jwt.JwtUtil;
import com.kakao.termproject.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PetService petService;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public String register(RegisterRequest request) {

    if(userRepository.findUserByEmail(request.email()).isPresent()){
      throw new EmailDuplicationException("중복된 이메일입니다");
    }

    String encodedPassword = passwordEncoder.encode(request.password());

    User user = new User(
        request.email(),
        request.username(),
        encodedPassword);

    userRepository.save(user);

    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    return jwtUtil.createAccessToken(user);
  }

  @Deprecated
  public String login(LoginRequest request) {
    User storedUser = userRepository.findUserByEmail(request.email())
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));
    if(!BCrypt.checkpw(request.password(), storedUser.getPassword())){
      throw new InvalidPasswordException("비밀번호가 다릅니다");
    }
    return jwtUtil.createAccessToken(storedUser);
  }

  @Transactional
  public void setPet(Long userId, PetCreateRequest request){
    Pet pet = petService.create(request);
    User user = userRepository.findById(userId).
        orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

    user.assignPet(pet);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findUserByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
