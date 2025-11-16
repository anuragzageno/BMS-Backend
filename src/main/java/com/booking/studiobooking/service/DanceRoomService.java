package com.booking.studiobooking.service;

import com.booking.studiobooking.model.Booking;
import com.booking.studiobooking.model.DanceRoom;
import com.booking.studiobooking.repository.BookingRepository;
import com.booking.studiobooking.repository.DanceRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class DanceRoomService {
    
    private final DanceRoomRepository danceRoomRepository;
    private final BookingRepository bookingRepository;
    
    @Autowired
    public DanceRoomService(DanceRoomRepository danceRoomRepository, BookingRepository bookingRepository) {
        this.danceRoomRepository = danceRoomRepository;
        this.bookingRepository = bookingRepository;
    }
    
    public List<DanceRoom> getAllDanceRooms() {
        return danceRoomRepository.findAll();
    }
    
    public List<DanceRoom> getDanceRoomsByStudio(Long studioId) {
        return danceRoomRepository.findByStudioId(studioId);
    }
    
    public List<DanceRoom> getAvailableDanceRoomsByStudio(Long studioId) {
        return danceRoomRepository.findByStudioIdAndIsAvailableTrue(studioId);
    }
    
    public List<DanceRoom> getDanceRoomsByStudioAndType(Long studioId, String roomType) {
        return danceRoomRepository.findByStudioIdAndRoomType(studioId, roomType);
    }
    
    public Optional<DanceRoom> getDanceRoomById(Long id) {
        return danceRoomRepository.findById(id);
    }
    
    public DanceRoom saveDanceRoom(DanceRoom danceRoom) {
        return danceRoomRepository.save(danceRoom);
    }
    
    public void deleteDanceRoom(Long id) {
        danceRoomRepository.deleteById(id);
    }

    public Map<String, Object> getDanceRoomAvailability(Long danceRoomId, LocalDate bookingDate) {
        Optional<DanceRoom> danceRoomOpt = danceRoomRepository.findById(danceRoomId);
        if (danceRoomOpt.isEmpty()) {
            throw new RuntimeException("Dance room not found");
        }

        DanceRoom danceRoom = danceRoomOpt.get();
        List<Booking> existingBookings = bookingRepository.findByDanceRoomIdAndBookingDate(danceRoomId, bookingDate);

        // Generate time slots from 6 AM to 11 PM
        List<String> timeSlots = generateTimeSlots();
        Map<String, Boolean> availability = new HashMap<>();

        for (String timeSlot : timeSlots) {
            boolean isAvailable = isTimeSlotAvailable(timeSlot, existingBookings);
            availability.put(timeSlot, isAvailable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("danceRoomId", danceRoomId);
        result.put("danceRoomName", danceRoom.getName());
        result.put("roomType", danceRoom.getRoomType());
        result.put("capacity", danceRoom.getCapacity());
        result.put("date", bookingDate.toString());
        result.put("availability", availability);
        
        return result;
    }

    private List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 6; hour < 23; hour++) {
            String startTime = String.format("%02d:00", hour);
            String endTime = String.format("%02d:00", hour + 1);
            timeSlots.add(startTime + "-" + endTime);
        }
        return timeSlots;
    }

    private boolean isTimeSlotAvailable(String timeSlot, List<Booking> existingBookings) {
        String[] times = timeSlot.split("-");
        LocalTime slotStart = LocalTime.parse(times[0]);
        LocalTime slotEnd = LocalTime.parse(times[1]);

        for (Booking booking : existingBookings) {
            LocalTime bookingStart = booking.getStartTime();
            LocalTime bookingEnd = booking.getEndTime();

            // Check if there's any overlap (but allow adjacent slots)
            // Slots are overlapping if one starts before the other ends AND the other starts before the first ends
            boolean overlapping = slotStart.isBefore(bookingEnd) && bookingStart.isBefore(slotEnd);
            
            if (overlapping) {
                return false; // There's an overlap, slot is not available
            }
        }
        return true; // No overlap found, slot is available
    }
}