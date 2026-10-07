package com.pharmatrack.pharmatrack.service;

import com.pharmatrack.pharmatrack.entity.Batch;
import com.pharmatrack.pharmatrack.entity.Sale;
import com.pharmatrack.pharmatrack.repository.BatchRepository;
import com.pharmatrack.pharmatrack.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final BatchRepository batchRepository;

    public SaleService(
            SaleRepository saleRepository,
            BatchRepository batchRepository
    ) {
        this.saleRepository = saleRepository;
        this.batchRepository = batchRepository;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    @Transactional
    public Sale addSale(Sale sale) {

        Batch batch;

        // FEFO: automatically select earliest-expiring batch
        if (sale.getMedicineId() != null && sale.getBatchId() == null) {

            List<Batch> batches =
                    batchRepository
                            .findByMedicineIdAndQuantityGreaterThanOrderByExpiryDateAsc(
                                    sale.getMedicineId(),
                                    0
                            );

            if (batches.isEmpty()) {
                throw new RuntimeException(
                        "No available stock for this medicine."
                );
            }

            batch = batches.get(0);

        } else {

            // Manual batch selection
            batch = batchRepository.findById(sale.getBatchId())
                    .orElseThrow(() ->
                            new RuntimeException("Batch not found.")
                    );
        }

        if (batch.getQuantity() < sale.getQuantity()) {
            throw new RuntimeException(
                    "Insufficient stock. Available quantity: "
                            + batch.getQuantity()
            );
        }

        sale.setMedicineId(batch.getMedicineId());
        sale.setBatchId(batch.getId());
        sale.setSellingPrice(batch.getSellingPrice());

        sale.setTotalAmount(
                batch.getSellingPrice() * sale.getQuantity()
        );

        sale.setSaleDate(LocalDateTime.now());

        // Deduct stock
        batch.setQuantity(
                batch.getQuantity() - sale.getQuantity()
        );

        batchRepository.save(batch);

        return saleRepository.save(sale);
    }

    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }
}