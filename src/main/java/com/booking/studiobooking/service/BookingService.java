package com.booking.studiobooking.service;

import com.booking.studiobooking.dto.BookingDto;
import com.booking.studiobooking.dto.CreateBookingRequest;
import com.booking.studiobooking.dto.UserDto;
import com.booking.studiobooking.model.Booking;
import com.booking.studiobooking.model.DanceRoom;
import com.booking.studiobooking.model.User;
import com.booking.studiobooking.repository.BookingRepository;
import com.booking.studiobooking.repository.DanceRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private DanceRoomRepository danceRoomRepository;
    
    @Autowired
    private UserService userService;

    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<BookingDto> getBookingById(Long id) {
        return bookingRepository.findById(id).map(this::convertToDto);
    }

    public Optional<BookingDto> getBookingByReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference)
                .map(this::convertToDto);
    }

    public List<BookingDto> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsByUserEmail(String email) {
        return bookingRepository.findByCustomerEmailOrderByBookingDateDesc(email).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsByDanceRoomAndDate(Long danceRoomId, LocalDate date) {
        return bookingRepository.findByDanceRoomIdAndBookingDate(danceRoomId, date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BookingDto createBooking(CreateBookingRequest request) {
        // Validate dance room exists
        DanceRoom danceRoom = danceRoomRepository.findById(request.getDanceRoomId())
                .orElseThrow(() -> new RuntimeException("Dance room not found"));

        // Check if the time slot is available
        if (!isTimeSlotAvailable(request.getDanceRoomId(), request.getBookingDate(), 
                                request.getStartTime(), request.getEndTime())) {
            throw new RuntimeException("Time slot is not available");
        }

        // Get or create user
        UserDto userDto = userService.getOrCreateUserByEmail(
                request.getCustomerEmail(), 
                request.getCustomerName(), 
                request.getCustomerPhone()
        );

        // Create booking
        Booking booking = new Booking();
        booking.setDanceRoom(danceRoom);
        booking.setUser(convertUserDtoToEntity(userDto));
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerEmail(request.getCustomerEmail());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setStatus("CONFIRMED");
        booking.setPaymentStatus("PENDING");
        booking.setBookingReference(generateBookingReference());
        
        // Calculate amount based on dance room hourly rate and duration
        long durationHours = java.time.Duration.between(request.getStartTime(), request.getEndTime()).toHours();
        if (durationHours == 0) durationHours = 1; // Minimum 1 hour
        booking.setAmountPaid(danceRoom.getHourlyRate().multiply(java.math.BigDecimal.valueOf(durationHours)));

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDto(savedBooking);
    }

    public BookingDto updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDto(updatedBooking);
    }

    public BookingDto updatePaymentStatus(Long id, String paymentStatus) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setPaymentStatus(paymentStatus);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDto(updatedBooking);
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    private boolean isTimeSlotAvailable(Long danceRoomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Booking> conflictingBookings = bookingRepository.findByDanceRoomIdAndBookingDateAndStatusNot(
                danceRoomId, date, "CANCELLED");
        
        return conflictingBookings.stream().noneMatch(booking -> 
                !(endTime.isBefore(booking.getStartTime()) || startTime.isAfter(booking.getEndTime()))
        );
    }

    private String generateBookingReference() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BookingDto convertToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setDanceRoomId(booking.getDanceRoom().getId());
        dto.setDanceRoomName(booking.getDanceRoom().getName());
        dto.setRoomType(booking.getDanceRoom().getRoomType());
        dto.setStudioId(booking.getDanceRoom().getStudio().getId());
        dto.setStudioName(booking.getDanceRoom().getStudio().getName());
        dto.setUserId(booking.getUser().getId());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerPhone(booking.getCustomerPhone());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setAmountPaid(booking.getAmountPaid());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setBookingReference(booking.getBookingReference());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }

    private User convertUserDtoToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setProfileImage(dto.getProfileImage());
        user.setIsActive(dto.getIsActive());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        return user;
    }
}