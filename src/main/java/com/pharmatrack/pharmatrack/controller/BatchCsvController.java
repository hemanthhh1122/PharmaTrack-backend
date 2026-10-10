
package com.pharmatrack.pharmatrack.controller;

import com.pharmatrack.pharmatrack.service.BatchCsvService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/csv/batches")
@CrossOrigin(origins = "http://localhost:5173")
public class BatchCsvController {

    private final BatchCsvService csvService;

    public BatchCsvController(BatchCsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBatches() {
        byte[] data = csvService.exportBatches()
            .getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=batches.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(data);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        String template =
            "medicineId,batchNumber,manufacturingDate,expiryDate,quantity,purchasePrice,sellingPrice\n";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=batches_template.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(template.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping(value = "/import",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importBatches(
            @RequestParam("file") MultipartFile file) throws IOException {

        int count = csvService.importBatches(file);

        return ResponseEntity.ok(Map.of(
            "message", "Batches imported successfully",
            "importedCount", count
        ));
    }
}
