
package com.pharmatrack.pharmatrack.service;

import com.pharmatrack.pharmatrack.entity.Batch;
import com.pharmatrack.pharmatrack.repository.BatchRepository;
import com.pharmatrack.pharmatrack.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchCsvService {

    private static final String HEADER =
        "medicineId,batchNumber,manufacturingDate,expiryDate,quantity,purchasePrice,sellingPrice";

    private final BatchRepository batchRepository;
    private final MedicineRepository medicineRepository;

    public BatchCsvService(
            BatchRepository batchRepository,
            MedicineRepository medicineRepository) {
        this.batchRepository = batchRepository;
        this.medicineRepository = medicineRepository;
    }

    public String exportBatches() {
        StringBuilder csv = new StringBuilder(HEADER).append("\n");

        for (Batch b : batchRepository.findAll()) {
            csv.append(b.getMedicineId()).append(",")
               .append(escape(b.getBatchNumber())).append(",")
               .append(b.getManufacturingDate()).append(",")
               .append(b.getExpiryDate()).append(",")
               .append(b.getQuantity()).append(",")
               .append(b.getPurchasePrice()).append(",")
               .append(b.getSellingPrice()).append("\n");
        }

        return csv.toString();
    }

    @Transactional
    public int importBatches(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a CSV file.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("CSV file must be 5 MB or smaller.");
        }

        List<Batch> importedBatches = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String header = reader.readLine();

            if (header == null ||
                !header.replace("\uFEFF", "").trim().equalsIgnoreCase(HEADER)) {
                throw new IllegalArgumentException(
                    "Incorrect CSV headers. Download the batch template.");
            }

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                try {
                    List<String> fields = parseLine(line);

                    if (fields.size() != 7) {
                        throw new IllegalArgumentException("Expected 7 columns.");
                    }

                    Long medicineId = Long.valueOf(fields.get(0).trim());

                    if (!medicineRepository.existsById(medicineId)) {
                        throw new IllegalArgumentException(
                            "Medicine ID " + medicineId + " does not exist.");
                    }

                    Batch batch = new Batch();
                    batch.setMedicineId(medicineId);
                    batch.setBatchNumber(fields.get(1).trim());
                    batch.setManufacturingDate(
                        LocalDate.parse(fields.get(2).trim()));
                    batch.setExpiryDate(
                        LocalDate.parse(fields.get(3).trim()));
                    batch.setQuantity(Integer.valueOf(fields.get(4).trim()));
                    batch.setPurchasePrice(Double.valueOf(fields.get(5).trim()));
                    batch.setSellingPrice(Double.valueOf(fields.get(6).trim()));

                    if (batch.getBatchNumber().isBlank()) {
                        throw new IllegalArgumentException("Batch number is required.");
                    }

                    if (batch.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Quantity must be greater than zero.");
                    }

                    if (!Double.isFinite(batch.getPurchasePrice()) ||
                        !Double.isFinite(batch.getSellingPrice()) ||
                        batch.getPurchasePrice() < 0 ||
                        batch.getSellingPrice() < 0) {
                        throw new IllegalArgumentException("Prices must be valid and non-negative.");
                    }

                    if (batch.getExpiryDate().isBefore(batch.getManufacturingDate())) {
                        throw new IllegalArgumentException(
                            "Expiry date cannot be before manufacturing date.");
                    }

                    importedBatches.add(batch);

                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                        "Invalid data at CSV line " + lineNumber + ": " +
                        e.getMessage(), e);
                }
            }
        }

        batchRepository.saveAll(importedBatches);
        return importedBatches.size();
    }

    private String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (quoted && i + 1 < line.length() &&
                    line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }

        if (quoted) {
            throw new IllegalArgumentException("Unclosed quoted field.");
        }

        fields.add(field.toString());
        return fields;
    }
}
