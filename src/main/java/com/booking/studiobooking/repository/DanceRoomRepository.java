package com.booking.studiobooking.repository;

import com.booking.studiobooking.model.DanceRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanceRoomRepository extends JpaRepository<DanceRoom, Long> {
    
    List<DanceRoom> findByStudioId(Long studioId);
    
    List<DanceRoom> findByStudioIdAndIsAvailableTrue(Long studioId);
    
    @Query("SELECT dr FROM DanceRoom dr WHERE dr.studio.id = :studioId AND dr.roomType = :roomType")
    List<DanceRoom> findByStudioIdAndRoomType(@Param("studioId") Long studioId, @Param("roomType") String roomType);
    
    @Query("SELECT dr FROM DanceRoom dr WHERE dr.studio.id = :studioId AND dr.roomType = :roomType AND dr.isAvailable = true")
    List<DanceRoom> findByStudioIdAndRoomTypeAndIsAvailableTrue(@Param("studioId") Long studioId, @Param("roomType") String roomType);
    
    List<DanceRoom> findByRoomType(String roomType);
    
    List<DanceRoom> findByCapacityGreaterThanEqual(Integer capacity);
}