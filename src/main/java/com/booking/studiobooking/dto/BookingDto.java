package com.booking.studiobooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Long danceRoomId;
    private String danceRoomName;
    private String roomType;
    private Long studioId;
    private String studioName;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private BigDecimal amountPaid;
    private String paymentStatus;
    private String bookingReference;
    private String specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}