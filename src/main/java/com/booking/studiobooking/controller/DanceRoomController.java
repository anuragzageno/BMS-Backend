package com.booking.studiobooking.controller;

import com.booking.studiobooking.dto.DanceRoomDto;
import com.booking.studiobooking.model.DanceRoom;
import com.booking.studiobooking.model.Studio;
import com.booking.studiobooking.service.DanceRoomService;
import com.booking.studiobooking.service.StudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dance-rooms")
@CrossOrigin(origins = "*")
public class DanceRoomController {
    
    private final DanceRoomService danceRoomService;
    private final StudioService studioService;
    
    @Autowired
    public DanceRoomController(DanceRoomService danceRoomService, StudioService studioService) {
        this.danceRoomService = danceRoomService;
        this.studioService = studioService;
    }
    
    @GetMapping
    public ResponseEntity<List<DanceRoomDto>> getAllDanceRooms() {
        List<DanceRoom> danceRooms = danceRoomService.getAllDanceRooms();
        List<DanceRoomDto> danceRoomDtos = danceRooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(danceRoomDtos);
    }
    
    @GetMapping("/studio/{studioId}")
    public ResponseEntity<List<DanceRoomDto>> getDanceRoomsByStudio(@PathVariable Long studioId) {
        List<DanceRoom> danceRooms = danceRoomService.getDanceRoomsByStudio(studioId);
        List<DanceRoomDto> danceRoomDtos = danceRooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(danceRoomDtos);
    }
    
    @GetMapping("/studio/{studioId}/available")
    public ResponseEntity<List<DanceRoomDto>> getAvailableDanceRoomsByStudio(@PathVariable Long studioId) {
        List<DanceRoom> danceRooms = danceRoomService.getAvailableDanceRoomsByStudio(studioId);
        List<DanceRoomDto> danceRoomDtos = danceRooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(danceRoomDtos);
    }
    
    @GetMapping("/studio/{studioId}/type/{roomType}")
    public ResponseEntity<List<DanceRoomDto>> getDanceRoomsByStudioAndType(
            @PathVariable Long studioId, 
            @PathVariable String roomType) {
        List<DanceRoom> danceRooms = danceRoomService.getDanceRoomsByStudioAndType(studioId, roomType);
        List<DanceRoomDto> danceRoomDtos = danceRooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(danceRoomDtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DanceRoomDto> getDanceRoomById(@PathVariable Long id) {
        Optional<DanceRoom> danceRoom = danceRoomService.getDanceRoomById(id);
        return danceRoom.map(value -> ResponseEntity.ok(convertToDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<DanceRoomDto> createDanceRoom(@RequestBody DanceRoomDto danceRoomDto) {
        Optional<Studio> studio = studioService.getStudioEntityById(danceRoomDto.getStudioId());
        if (studio.isPresent()) {
            DanceRoom danceRoom = convertToEntity(danceRoomDto, studio.get());
            DanceRoom savedDanceRoom = danceRoomService.saveDanceRoom(danceRoom);
            return new ResponseEntity<>(convertToDto(savedDanceRoom), HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DanceRoomDto> updateDanceRoom(@PathVariable Long id, @RequestBody DanceRoomDto danceRoomDto) {
        Optional<DanceRoom> existingDanceRoom = danceRoomService.getDanceRoomById(id);
        if (existingDanceRoom.isPresent()) {
            Optional<Studio> studio = studioService.getStudioEntityById(danceRoomDto.getStudioId());
            if (studio.isPresent()) {
                DanceRoom danceRoom = convertToEntity(danceRoomDto, studio.get());
                danceRoom.setId(id);
                DanceRoom updatedDanceRoom = danceRoomService.saveDanceRoom(danceRoom);
                return ResponseEntity.ok(convertToDto(updatedDanceRoom));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDanceRoom(@PathVariable Long id) {
        Optional<DanceRoom> existingDanceRoom = danceRoomService.getDanceRoomById(id);
        if (existingDanceRoom.isPresent()) {
            danceRoomService.deleteDanceRoom(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{danceRoomId}/availability")
    public ResponseEntity<Map<String, Object>> getDanceRoomAvailability(
            @PathVariable Long danceRoomId,
            @RequestParam String date) {
        try {
            LocalDate bookingDate = LocalDate.parse(date);
            Map<String, Object> availability = danceRoomService.getDanceRoomAvailability(danceRoomId, bookingDate);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private DanceRoomDto convertToDto(DanceRoom danceRoom) {
        DanceRoomDto danceRoomDto = new DanceRoomDto();
        danceRoomDto.setId(danceRoom.getId());
        danceRoomDto.setStudioId(danceRoom.getStudio().getId());
        danceRoomDto.setStudioName(danceRoom.getStudio().getName());
        danceRoomDto.setName(danceRoom.getName());
        danceRoomDto.setRoomType(danceRoom.getRoomType());
        danceRoomDto.setHourlyRate(danceRoom.getHourlyRate());
        danceRoomDto.setIsAvailable(danceRoom.getIsAvailable());
        danceRoomDto.setCapacity(danceRoom.getCapacity());
        danceRoomDto.setAmenities(danceRoom.getAmenities());
        return danceRoomDto;
    }
    
    private DanceRoom convertToEntity(DanceRoomDto danceRoomDto, Studio studio) {
        DanceRoom danceRoom = new DanceRoom();
        danceRoom.setId(danceRoomDto.getId());
        danceRoom.setStudio(studio);
        danceRoom.setName(danceRoomDto.getName());
        danceRoom.setRoomType(danceRoomDto.getRoomType());
        danceRoom.setHourlyRate(danceRoomDto.getHourlyRate());
        danceRoom.setIsAvailable(danceRoomDto.getIsAvailable());
        danceRoom.setCapacity(danceRoomDto.getCapacity());
        danceRoom.setAmenities(danceRoomDto.getAmenities());
        return danceRoom;
    }
}