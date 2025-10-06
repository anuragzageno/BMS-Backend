package com.booking.studiobooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudioDto {
    
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String contactPhone;
    private String contactEmail;
    private BigDecimal rating;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Boolean isActive;
    
}