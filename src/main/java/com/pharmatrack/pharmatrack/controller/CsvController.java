
package com.pharmatrack.pharmatrack.controller;

import com.pharmatrack.pharmatrack.service.CsvService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin(origins = "http://localhost:5173")
public class CsvController {

    private final CsvService csvService;

    public CsvController(CsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping("/medicines/export")
    public ResponseEntity<byte[]> exportMedicines() {
        byte[] data = csvService.exportMedicines().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=medicines.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(data);
    }

    @GetMapping("/medicines/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        String template = "medicineName,genericName,category,dosageForm,strength,manufacturer,reorderLevel\n";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=medicines_template.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(template.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping(value = "/medicines/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importMedicines(
            @RequestParam("file") MultipartFile file) throws IOException {
        int count = csvService.importMedicines(file);
        return ResponseEntity.ok(Map.of(
            "message", "Medicines imported successfully",
            "importedCount", count
        ));
    }
}
