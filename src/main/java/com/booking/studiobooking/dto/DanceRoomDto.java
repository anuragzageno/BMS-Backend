package com.booking.studiobooking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanceRoomDto {
    private Long id;
    private Long studioId;
    private String studioName;
    private String name;
    private String roomType; // BIG_HALL, SMALL_HALL, REEL_BOOTH
    private BigDecimal hourlyRate;
    private Boolean isAvailable;
    private Integer capacity;
    private String amenities;
}