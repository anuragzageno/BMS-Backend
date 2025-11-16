package com.booking.studiobooking.repository;

import com.booking.studiobooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b FROM Booking b WHERE b.danceRoom.id = :danceRoomId")
    List<Booking> findByDanceRoomId(@Param("danceRoomId") Long danceRoomId);
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByCustomerEmailOrderByBookingDateDesc(String customerEmail);
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByDanceRoomIdAndBookingDate(Long danceRoomId, LocalDate bookingDate);
    
    List<Booking> findByDanceRoomIdAndBookingDateAndStatusNot(Long danceRoomId, LocalDate bookingDate, String status);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate = :bookingDate AND b.danceRoom.id = :danceRoomId")
    List<Booking> findByBookingDateAndDanceRoomId(@Param("bookingDate") LocalDate bookingDate, @Param("danceRoomId") Long danceRoomId);
    
    @Query("SELECT b FROM Booking b WHERE b.danceRoom.id = :danceRoomId AND b.bookingDate = :date " +
           "AND ((b.startTime <= :startTime AND b.endTime > :startTime) OR " +
           "(b.startTime < :endTime AND b.endTime >= :endTime) OR " +
           "(b.startTime >= :startTime AND b.endTime <= :endTime))")
    List<Booking> findOverlappingBookings(@Param("danceRoomId") Long danceRoomId, 
                                         @Param("date") LocalDate date, 
                                         @Param("startTime") LocalTime startTime, 
                                         @Param("endTime") LocalTime endTime);
    
}