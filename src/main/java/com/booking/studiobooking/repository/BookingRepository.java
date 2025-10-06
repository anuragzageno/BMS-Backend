package com.booking.studiobooking.repository;

import com.booking.studiobooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId")
    List<Booking> findByCourtId(@Param("courtId") Long courtId);
    
    List<Booking> findByUserId(Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate = :bookingDate AND b.court.id = :courtId")
    List<Booking> findByBookingDateAndCourtId(@Param("bookingDate") LocalDate bookingDate, @Param("courtId") Long courtId);
    
    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId AND b.bookingDate = :date " +
           "AND ((b.startTime <= :startTime AND b.endTime > :startTime) OR " +
           "(b.startTime < :endTime AND b.endTime >= :endTime) OR " +
           "(b.startTime >= :startTime AND b.endTime <= :endTime))")
    List<Booking> findOverlappingBookings(@Param("courtId") Long courtId, 
                                         @Param("date") LocalDate date, 
                                         @Param("startTime") LocalTime startTime, 
                                         @Param("endTime") LocalTime endTime);
    
}