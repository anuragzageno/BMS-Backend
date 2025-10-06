package com.booking.studiobooking.service;

import com.booking.studiobooking.model.Studio;
import com.booking.studiobooking.repository.StudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudioService {
    
    private final StudioRepository studioRepository;
    
    @Autowired
    public StudioService(StudioRepository studioRepository) {
        this.studioRepository = studioRepository;
    }
    
    public List<Studio> getAllStudios() {
        return studioRepository.findAll();
    }
    
    public List<Studio> getActiveStudios() {
        return studioRepository.findByIsActiveTrue();
    }
    
    public List<Studio> getStudiosByCity(String city) {
        return studioRepository.findByCity(city);
    }
    
    public Optional<Studio> getStudioById(Long id) {
        return studioRepository.findById(id);
    }
    
    public Studio saveStudio(Studio studio) {
        return studioRepository.save(studio);
    }
    
    public void deleteStudio(Long id) {
        studioRepository.deleteById(id);
    }
}