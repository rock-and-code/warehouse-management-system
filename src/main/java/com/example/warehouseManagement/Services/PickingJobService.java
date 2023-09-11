package com.example.warehouseManagement.Services;

import java.util.Optional;

import com.example.warehouseManagement.Domains.PickingJob;

public interface PickingJobService {
    public Iterable<PickingJob> findAll();
    public Optional<PickingJob> findById(Long id);
    public PickingJob save(PickingJob pickingJob);
    public void delete(PickingJob pickingJob);
}
