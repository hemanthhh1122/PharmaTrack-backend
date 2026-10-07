package com.pharmatrack.pharmatrack.repository;

import com.pharmatrack.pharmatrack.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}