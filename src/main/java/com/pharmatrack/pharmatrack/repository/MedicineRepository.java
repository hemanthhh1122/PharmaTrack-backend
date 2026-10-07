package com.pharmatrack.pharmatrack.repository;

import com.pharmatrack.pharmatrack.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}