package com.pharmatrack.pharmatrack.controller;

import com.pharmatrack.pharmatrack.entity.Batch;
import com.pharmatrack.pharmatrack.service.BatchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@CrossOrigin(origins = "http://localhost:5173")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping
    public List<Batch> getAllBatches() {
        return batchService.getAllBatches();
    }

    @PostMapping
    public Batch addBatch(@RequestBody Batch batch) {
        return batchService.addBatch(batch);
    }

    @DeleteMapping("/{id}")
    public void deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
    }
}