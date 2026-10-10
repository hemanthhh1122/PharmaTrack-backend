
package com.pharmatrack.pharmatrack.service;

import com.pharmatrack.pharmatrack.entity.Medicine;
import com.pharmatrack.pharmatrack.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CsvService {

    private final MedicineRepository repository;

    public CsvService(MedicineRepository repository) {
        this.repository = repository;
    }

    public String exportMedicines() {
        StringBuilder csv = new StringBuilder();
        csv.append("medicineName,genericName,category,dosageForm,strength,manufacturer,reorderLevel\n");

        for (Medicine m : repository.findAll()) {
            csv.append(escape(m.getMedicineName())).append(",")
               .append(escape(m.getGenericName())).append(",")
               .append(escape(m.getCategory())).append(",")
               .append(escape(m.getDosageForm())).append(",")
               .append(escape(m.getStrength())).append(",")
               .append(escape(m.getManufacturer())).append(",")
               .append(m.getReorderLevel() == null ? "" : m.getReorderLevel())
               .append("\n");
        }

        return csv.toString();
    }

    public int importMedicines(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a CSV file.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("CSV file must be 5 MB or smaller.");
        }

        int imported = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String header = reader.readLine();
            if (header == null ||
                !header.replace("\uFEFF", "").trim().equalsIgnoreCase(
                    "medicineName,genericName,category,dosageForm,strength,manufacturer,reorderLevel")) {
                throw new IllegalArgumentException("CSV headers do not match the required template.");
            }

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                String[] fields = line.split(",", -1);
                if (fields.length != 7) {
                    throw new IllegalArgumentException("Invalid CSV format at line " + lineNumber);
                }

                Medicine medicine = new Medicine();
                medicine.setMedicineName(unquote(fields[0]).trim());
                medicine.setGenericName(unquote(fields[1]).trim());
                medicine.setCategory(unquote(fields[2]).trim());
                medicine.setDosageForm(unquote(fields[3]).trim());
                medicine.setStrength(unquote(fields[4]).trim());
                medicine.setManufacturer(unquote(fields[5]).trim());

                String reorder = unquote(fields[6]).trim();
                try {
                    medicine.setReorderLevel(reorder.isEmpty() ? null : Integer.valueOf(reorder));
                    if (medicine.getReorderLevel() != null && medicine.getReorderLevel() < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid reorderLevel at line " + lineNumber);
                }

                if (medicine.getMedicineName().isBlank()) {
                    throw new IllegalArgumentException("Medicine name is required at line " + lineNumber);
                }

                repository.save(medicine);
                imported++;
            }
        }

        return imported;
    }

    private String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String unquote(String value) {
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }
}
