package com.booking.studiobooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dance_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanceRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studio_id", nullable = false)
    private Studio studio;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "room_type", nullable = false)
    private String roomType; // BIG_HALL, SMALL_HALL, REEL_BOOTH
    
    @Column(name = "hourly_rate", nullable = false)
    private BigDecimal hourlyRate;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    private Integer capacity;
    
    private String amenities;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}