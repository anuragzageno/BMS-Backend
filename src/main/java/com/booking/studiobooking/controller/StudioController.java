package com.booking.studiobooking.controller;

import com.booking.studiobooking.dto.StudioDto;
import com.booking.studiobooking.service.StudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        List<StudioDto> studios = studioService.getAllStudios();
        return ResponseEntity.ok(studios);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<StudioDto>> getActiveStudios() {
        List<StudioDto> studios = studioService.getActiveStudios();
        return ResponseEntity.ok(studios);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<StudioDto>> getStudiosByCity(@PathVariable String city) {
        List<StudioDto> studios = studioService.getStudiosByCity(city);
        return ResponseEntity.ok(studios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StudioDto> getStudioById(@PathVariable Long id) {
        Optional<StudioDto> studio = studioService.getStudioById(id);
        return studio.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-courts")
    public ResponseEntity<StudioDto> getStudioWithCourtsById(@PathVariable Long id) {
        Optional<StudioDto> studio = studioService.getStudioWithCourtsById(id);
        return studio.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
}