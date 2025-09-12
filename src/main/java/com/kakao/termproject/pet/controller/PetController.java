package com.kakao.termproject.pet.controller;

import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.pet.dto.PetUpdateRequest;
import com.kakao.termproject.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
@Slf4j
public class PetController {

  private final PetService petService;

  @PatchMapping("/{petId}}")
  public ResponseEntity<Void> updatePet(
      @PathVariable("petId") Long petId,
      @RequestBody @Valid PetUpdateRequest request
  ){
    petService.update(petId, request);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/{petId}}")
  public ResponseEntity<Pet> getPet(
      @PathVariable("petId") Long petId
  ){
    return ResponseEntity.status(HttpStatus.OK)
        .body(petService.get(petId));
  }

  @DeleteMapping("/{petId}")
  public ResponseEntity<Void> deletePet(
      @PathVariable("petId") Long petId
  ){
    petService.delete(petId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
