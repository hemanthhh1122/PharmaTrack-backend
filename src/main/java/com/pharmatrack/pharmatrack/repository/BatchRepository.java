package com.pharmatrack.pharmatrack.repository;

import com.pharmatrack.pharmatrack.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByMedicineIdAndQuantityGreaterThanOrderByExpiryDateAsc(
            Long medicineId,
            Integer quantity
    );
}