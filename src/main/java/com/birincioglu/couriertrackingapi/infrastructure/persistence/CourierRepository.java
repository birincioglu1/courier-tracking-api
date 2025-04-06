package com.birincioglu.couriertrackingapi.infrastructure.persistence;

import com.birincioglu.couriertrackingapi.domain.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Courier c WHERE c.id = :id")
    Optional<Courier> findByIdWithPessimisticWriteLock(@Param("id") Long id);

}
