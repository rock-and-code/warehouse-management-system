package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.DTOs.PickingJobDto;

public interface PickingJobService {
    public Iterable<PickingJob> findAll();
    public List<PickingJob> findAllPending();
    public Optional<PickingJob> findById(Long id);
    public PickingJob save(PickingJob pickingJob);
    public void delete(PickingJob pickingJob);
    public PickingJob fulfill(PickingJob pickingJob, PickingJobDto pickingJobDto);
}
