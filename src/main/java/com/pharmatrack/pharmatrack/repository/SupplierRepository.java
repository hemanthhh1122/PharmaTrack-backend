package com.pharmatrack.pharmatrack.repository;

import com.pharmatrack.pharmatrack.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}