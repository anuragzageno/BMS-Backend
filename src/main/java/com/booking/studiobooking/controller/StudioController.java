package com.booking.studiobooking.controller;

import com.booking.studiobooking.dto.StudioDto;
import com.booking.studiobooking.model.Studio;
import com.booking.studiobooking.service.StudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/studios")
@CrossOrigin(origins = "*")
public class StudioController {
    
    private final StudioService studioService;
    
    @Autowired
    public StudioController(StudioService studioService) {
        this.studioService = studioService;
    }
    
    @GetMapping
    public ResponseEntity<List<StudioDto>> getAllStudios() {
        List<Studio> studios = studioService.getAllStudios();
        List<StudioDto> studioDtos = studios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studioDtos);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<StudioDto>> getActiveStudios() {
        List<Studio> studios = studioService.getActiveStudios();
        List<StudioDto> studioDtos = studios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studioDtos);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<StudioDto>> getStudiosByCity(@PathVariable String city) {
        List<Studio> studios = studioService.getStudiosByCity(city);
        List<StudioDto> studioDtos = studios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studioDtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StudioDto> getStudioById(@PathVariable Long id) {
        Optional<Studio> studio = studioService.getStudioById(id);
        return studio.map(value -> ResponseEntity.ok(convertToDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<StudioDto> createStudio(@RequestBody StudioDto studioDto) {
        Studio studio = convertToEntity(studioDto);
        Studio savedStudio = studioService.saveStudio(studio);
        return new ResponseEntity<>(convertToDto(savedStudio), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StudioDto> updateStudio(@PathVariable Long id, @RequestBody StudioDto studioDto) {
        Optional<Studio> existingStudio = studioService.getStudioById(id);
        if (existingStudio.isPresent()) {
            Studio studio = convertToEntity(studioDto);
            studio.setId(id);
            Studio updatedStudio = studioService.saveStudio(studio);
            return ResponseEntity.ok(convertToDto(updatedStudio));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudio(@PathVariable Long id) {
        Optional<Studio> existingStudio = studioService.getStudioById(id);
        if (existingStudio.isPresent()) {
            studioService.deleteStudio(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    private StudioDto convertToDto(Studio studio) {
        StudioDto studioDto = new StudioDto();
        studioDto.setId(studio.getId());
        studioDto.setName(studio.getName());
        studioDto.setAddress(studio.getAddress());
        studioDto.setCity(studio.getCity());
        studioDto.setState(studio.getState());
        studioDto.setPostalCode(studio.getPostalCode());
        studioDto.setContactPhone(studio.getContactPhone());
        studioDto.setContactEmail(studio.getContactEmail());
        studioDto.setRating(studio.getRating());
        studioDto.setOpeningTime(studio.getOpeningTime());
        studioDto.setClosingTime(studio.getClosingTime());
        studioDto.setIsActive(studio.getIsActive());
        return studioDto;
    }
    
    private Studio convertToEntity(StudioDto studioDto) {
        Studio studio = new Studio();
        studio.setId(studioDto.getId());
        studio.setName(studioDto.getName());
        studio.setAddress(studioDto.getAddress());
        studio.setCity(studioDto.getCity());
        studio.setState(studioDto.getState());
        studio.setPostalCode(studioDto.getPostalCode());
        studio.setContactPhone(studioDto.getContactPhone());
        studio.setContactEmail(studioDto.getContactEmail());
        studio.setRating(studioDto.getRating());
        studio.setOpeningTime(studioDto.getOpeningTime());
        studio.setClosingTime(studioDto.getClosingTime());
        studio.setIsActive(studioDto.getIsActive());
        return studio;
    }
}