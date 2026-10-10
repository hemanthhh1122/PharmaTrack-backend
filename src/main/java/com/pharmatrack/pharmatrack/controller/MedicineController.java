
package com.pharmatrack.pharmatrack.controller;

import com.pharmatrack.pharmatrack.entity.Medicine;
import com.pharmatrack.pharmatrack.service.MedicineService;
import com.pharmatrack.pharmatrack.service.MedicineLookupService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "http://localhost:5173")
public class MedicineController {

    private final MedicineService medicineService;
    private final MedicineLookupService medicineLookupService;

    public MedicineController(
            MedicineService medicineService,
            MedicineLookupService medicineLookupService) {
        this.medicineService = medicineService;
        this.medicineLookupService = medicineLookupService;
    }

    // Get all medicines
    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medicineService.getAllMedicines();
    }

    // Search external medicine information
    @GetMapping("/lookup")
    public List<Map<String, String>> lookupMedicine(
            @RequestParam String query) {
        return medicineLookupService.search(query);
    }

    // Add a medicine
    @PostMapping
    public Medicine addMedicine(@RequestBody Medicine medicine) {
        return medicineService.addMedicine(medicine);
    }

    // Update a medicine
    @PutMapping("/{id}")
    public Medicine updateMedicine(
            @PathVariable Long id,
            @RequestBody Medicine medicine) {
        return medicineService.updateMedicine(id, medicine);
    }

    // Delete a medicine
    @DeleteMapping("/{id}")
    public void deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
    }
}
