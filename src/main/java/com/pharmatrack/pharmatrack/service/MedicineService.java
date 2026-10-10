
package com.pharmatrack.pharmatrack.service;

import com.pharmatrack.pharmatrack.entity.Medicine;
import com.pharmatrack.pharmatrack.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine addMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public Medicine updateMedicine(Long id, Medicine updatedMedicine) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Medicine not found: " + id));

        medicine.setMedicineName(updatedMedicine.getMedicineName());
        medicine.setGenericName(updatedMedicine.getGenericName());
        medicine.setCategory(updatedMedicine.getCategory());
        medicine.setDosageForm(updatedMedicine.getDosageForm());
        medicine.setStrength(updatedMedicine.getStrength());
        medicine.setManufacturer(updatedMedicine.getManufacturer());
        medicine.setReorderLevel(updatedMedicine.getReorderLevel());

        return medicineRepository.save(medicine);
    }

    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }
}
