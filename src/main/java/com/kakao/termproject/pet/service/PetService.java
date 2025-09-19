package com.kakao.termproject.pet.service;

import com.kakao.termproject.exception.custom.PetNotFoundException;
import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.pet.dto.PetCreateRequest;
import com.kakao.termproject.pet.dto.PetUpdateRequest;
import com.kakao.termproject.pet.repository.PetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

  private final PetRepository petRepository;

  @Transactional
  public Pet create(PetCreateRequest request) {
    Pet pet = Pet.builder().
        name(request.name()).
        breed(request.breed()).
        gender(request.gender()).
        birthDate(request.birthDate()).
        isNeutered(request.isNeutered()).
        isVaccinated(request.isVaccinated()).
        weight(request.weight()).
        preferredWeather(request.preferredWeather()).
        preferredPath(request.preferredPath()).
        chronicDisease(request.chronicDisease()).
        build();

    return petRepository.save(pet);
  }

  public Pet get(Long petId) {
    return petRepository.findById(petId).
        orElseThrow(() -> new PetNotFoundException("해당 반려견을 찾을 수 없습니다"));
  }

  @Transactional
  public void update(Long petId, PetUpdateRequest request) {
    Pet pet = get(petId);
    pet.updatePet(request);
  }
  @Transactional
  public void delete(Long petId) {
    petRepository.deleteById(petId);
  }

}
