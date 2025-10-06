package com.booking.studiobooking.service;

import com.booking.studiobooking.model.Court;
import com.booking.studiobooking.repository.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourtService {
    
    private final CourtRepository courtRepository;
    
    @Autowired
    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }
    
    public List<Court> getAllCourts() {
        return courtRepository.findAll();
    }
    
    public List<Court> getCourtsByStudio(Long studioId) {
        return courtRepository.findByStudioId(studioId);
    }
    
    public List<Court> getAvailableCourtsByStudio(Long studioId) {
        return courtRepository.findByStudioIdAndIsAvailableTrue(studioId);
    }
    
    public Optional<Court> getCourtById(Long id) {
        return courtRepository.findById(id);
    }
    
    public Court saveCourt(Court court) {
        return courtRepository.save(court);
    }
    
    public void deleteCourt(Long id) {
        courtRepository.deleteById(id);
    }
}