package com.booking.studiobooking.repository;

import com.booking.studiobooking.model.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    
    List<Court> findByStudioId(Long studioId);
    
    List<Court> findByStudioIdAndIsAvailableTrue(Long studioId);
    
}