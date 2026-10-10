
package com.pharmatrack.pharmatrack.service;

import com.pharmatrack.pharmatrack.entity.Supplier;
import com.pharmatrack.pharmatrack.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier addSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Supplier not found: " + id));

        supplier.setSupplierName(updatedSupplier.getSupplierName());
        supplier.setPhone(updatedSupplier.getPhone());
        supplier.setEmail(updatedSupplier.getEmail());
        supplier.setAddress(updatedSupplier.getAddress());

        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }
}
