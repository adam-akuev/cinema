package com.akuev.repository;

import com.akuev.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(UUID userId);
    Optional<Booking> findByIdAndUserId(Long id, UUID userId);
    boolean existsByIdAndUserId(Long id, UUID userId);
}
