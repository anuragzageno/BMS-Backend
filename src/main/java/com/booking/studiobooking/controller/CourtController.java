package com.booking.studiobooking.controller;

import com.booking.studiobooking.dto.CourtDto;
import com.booking.studiobooking.model.Court;
import com.booking.studiobooking.model.Studio;
import com.booking.studiobooking.service.CourtService;
import com.booking.studiobooking.service.StudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courts")
@CrossOrigin(origins = "*")
public class CourtController {
    
    private final CourtService courtService;
    private final StudioService studioService;
    
    @Autowired
    public CourtController(CourtService courtService, StudioService studioService) {
        this.courtService = courtService;
        this.studioService = studioService;
    }
    
    @GetMapping
    public ResponseEntity<List<CourtDto>> getAllCourts() {
        List<Court> courts = courtService.getAllCourts();
        List<CourtDto> courtDtos = courts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courtDtos);
    }
    
    @GetMapping("/studio/{studioId}")
    public ResponseEntity<List<CourtDto>> getCourtsByStudio(@PathVariable Long studioId) {
        List<Court> courts = courtService.getCourtsByStudio(studioId);
        List<CourtDto> courtDtos = courts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courtDtos);
    }
    
    @GetMapping("/studio/{studioId}/available")
    public ResponseEntity<List<CourtDto>> getAvailableCourtsByStudio(@PathVariable Long studioId) {
        List<Court> courts = courtService.getAvailableCourtsByStudio(studioId);
        List<CourtDto> courtDtos = courts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courtDtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourtDto> getCourtById(@PathVariable Long id) {
        Optional<Court> court = courtService.getCourtById(id);
        return court.map(value -> ResponseEntity.ok(convertToDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<CourtDto> createCourt(@RequestBody CourtDto courtDto) {
        Optional<Studio> studio = studioService.getStudioById(courtDto.getStudioId());
        if (studio.isPresent()) {
            Court court = convertToEntity(courtDto, studio.get());
            Court savedCourt = courtService.saveCourt(court);
            return new ResponseEntity<>(convertToDto(savedCourt), HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CourtDto> updateCourt(@PathVariable Long id, @RequestBody CourtDto courtDto) {
        Optional<Court> existingCourt = courtService.getCourtById(id);
        if (existingCourt.isPresent()) {
            Optional<Studio> studio = studioService.getStudioById(courtDto.getStudioId());
            if (studio.isPresent()) {
                Court court = convertToEntity(courtDto, studio.get());
                court.setId(id);
                Court updatedCourt = courtService.saveCourt(court);
                return ResponseEntity.ok(convertToDto(updatedCourt));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourt(@PathVariable Long id) {
        Optional<Court> existingCourt = courtService.getCourtById(id);
        if (existingCourt.isPresent()) {
            courtService.deleteCourt(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    private CourtDto convertToDto(Court court) {
        CourtDto courtDto = new CourtDto();
        courtDto.setId(court.getId());
        courtDto.setStudioId(court.getStudio().getId());
        courtDto.setStudioName(court.getStudio().getName());
        courtDto.setName(court.getName());
        courtDto.setCourtType(court.getCourtType());
        courtDto.setHourlyRate(court.getHourlyRate());
        courtDto.setIsAvailable(court.getIsAvailable());
        courtDto.setCapacity(court.getCapacity());
        courtDto.setAmenities(court.getAmenities());
        return courtDto;
    }
    
    private Court convertToEntity(CourtDto courtDto, Studio studio) {
        Court court = new Court();
        court.setId(courtDto.getId());
        court.setStudio(studio);
        court.setName(courtDto.getName());
        court.setCourtType(courtDto.getCourtType());
        court.setHourlyRate(courtDto.getHourlyRate());
        court.setIsAvailable(courtDto.getIsAvailable());
        court.setCapacity(courtDto.getCapacity());
        court.setAmenities(courtDto.getAmenities());
        return court;
    }
}