package com.booking.studiobooking.service;

import com.booking.studiobooking.dto.StudioDto;
import com.booking.studiobooking.dto.CourtDto;
import com.booking.studiobooking.model.Studio;
import com.booking.studiobooking.model.Court;
import com.booking.studiobooking.repository.StudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudioService {
    
    private final StudioRepository studioRepository;
    
    @Autowired
    public StudioService(StudioRepository studioRepository) {
        this.studioRepository = studioRepository;
    }
    
    public List<StudioDto> getAllStudios() {
        return studioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<StudioDto> getActiveStudios() {
        return studioRepository.findByIsActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<StudioDto> getStudiosByCity(String city) {
        return studioRepository.findByCity(city).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<StudioDto> getStudioById(Long id) {
        return studioRepository.findById(id).map(this::convertToDto);
    }
    
    public Optional<StudioDto> getStudioWithCourtsById(Long id) {
        return studioRepository.findById(id).map(this::convertToDtoWithCourts);
    }
    
    public Optional<Studio> getStudioEntityById(Long id) {
        return studioRepository.findById(id);
    }
    
    public Studio saveStudio(Studio studio) {
        return studioRepository.save(studio);
    }
    
    public void deleteStudio(Long id) {
        studioRepository.deleteById(id);
    }
    
    private StudioDto convertToDto(Studio studio) {
        StudioDto dto = new StudioDto();
        dto.setId(studio.getId());
        dto.setName(studio.getName());
        dto.setAddress(studio.getAddress());
        dto.setCity(studio.getCity());
        dto.setState(studio.getState());
        dto.setPostalCode(studio.getPostalCode());
        dto.setContactPhone(studio.getContactPhone());
        dto.setContactEmail(studio.getContactEmail());
        dto.setRating(studio.getRating());
        dto.setOpeningTime(studio.getOpeningTime());
        dto.setClosingTime(studio.getClosingTime());
        dto.setIsActive(studio.getIsActive());
        dto.setCreatedAt(studio.getCreatedAt());
        dto.setUpdatedAt(studio.getUpdatedAt());
        dto.setDescription(studio.getDescription());
        dto.setImageUrl(studio.getImageUrl());
        dto.setFacilities(studio.getFacilities());
        dto.setPricingInfo(studio.getPricingInfo());
        return dto;
    }
    
    private StudioDto convertToDtoWithCourts(Studio studio) {
        StudioDto dto = convertToDto(studio);
        if (studio.getCourts() != null) {
            List<CourtDto> courtDtos = studio.getCourts().stream()
                    .map(this::convertCourtToDto)
                    .collect(Collectors.toList());
            dto.setCourts(courtDtos);
        }
        return dto;
    }
    
    private CourtDto convertCourtToDto(Court court) {
        CourtDto dto = new CourtDto();
        dto.setId(court.getId());
        dto.setStudioId(court.getStudio().getId());
        dto.setStudioName(court.getStudio().getName());
        dto.setName(court.getName());
        dto.setCourtType(court.getCourtType());
        dto.setHourlyRate(court.getHourlyRate());
        dto.setIsAvailable(court.getIsAvailable());
        dto.setCapacity(court.getCapacity());
        dto.setAmenities(court.getAmenities());
        dto.setCreatedAt(court.getCreatedAt());
        dto.setUpdatedAt(court.getUpdatedAt());
        return dto;
    }
}