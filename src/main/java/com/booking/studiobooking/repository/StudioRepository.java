package com.booking.studiobooking.repository;

import com.booking.studiobooking.model.Studio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudioRepository extends JpaRepository<Studio, Long> {
    
    List<Studio> findByCity(String city);
    
    List<Studio> findByIsActiveTrue();
    
}