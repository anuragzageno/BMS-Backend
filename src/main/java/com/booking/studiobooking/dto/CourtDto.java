package com.booking.studiobooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourtDto {
    
    private Long id;
    private Long studioId;
    private String studioName;
    private String name;
    private String courtType;
    private BigDecimal hourlyRate;
    private Boolean isAvailable;
    private Integer capacity;
    private String amenities;
    
}