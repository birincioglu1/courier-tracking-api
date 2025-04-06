package com.birincioglu.couriertrackingapi.infrastructure.persistence;

import com.birincioglu.couriertrackingapi.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

}
