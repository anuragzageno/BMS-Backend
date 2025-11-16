package com.booking.studiobooking.service;

import com.booking.studiobooking.model.Booking;
import com.booking.studiobooking.model.Court;
import com.booking.studiobooking.repository.BookingRepository;
import com.booking.studiobooking.repository.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class CourtService {
    
    private final CourtRepository courtRepository;
    private final BookingRepository bookingRepository;
    
    @Autowired
    public CourtService(CourtRepository courtRepository, BookingRepository bookingRepository) {
        this.courtRepository = courtRepository;
        this.bookingRepository = bookingRepository;
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

    public Map<String, Object> getCourtAvailability(Long courtId, LocalDate bookingDate) {
        Optional<Court> courtOpt = courtRepository.findById(courtId);
        if (courtOpt.isEmpty()) {
            throw new RuntimeException("Court not found");
        }

        Court court = courtOpt.get();
        List<Booking> existingBookings = bookingRepository.findByDanceRoomIdAndBookingDate(courtId, bookingDate);

        // Generate time slots from 6 AM to 11 PM
        List<String> timeSlots = generateTimeSlots();
        Map<String, Boolean> availability = new HashMap<>();

        for (String timeSlot : timeSlots) {
            boolean isAvailable = isTimeSlotAvailable(timeSlot, existingBookings);
            availability.put(timeSlot, isAvailable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("courtId", courtId);
        result.put("courtName", court.getName());
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

            // Check if there's any overlap
            if (!(slotEnd.isBefore(bookingStart) || slotStart.isAfter(bookingEnd))) {
                return false; // There's an overlap, slot is not available
            }
        }
        return true; // No overlap found, slot is available
    }
}