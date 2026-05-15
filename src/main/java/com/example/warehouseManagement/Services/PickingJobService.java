package com.example.warehouseManagement.Services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.DTOs.AdvancedPickingJobSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.PickingJobDto;

public interface PickingJobService {
    public Iterable<PickingJob> findAll();
    /**
     * Advanced search — empty criteria returns every picking job, paginated.
     * Spec returns cb.conjunction() when nothing is set.
     */
    Page<PickingJob> findAdvanced(AdvancedPickingJobSearchCriteria criteria, Pageable pageable);
    public Optional<PickingJob> findById(Long id);
    public PickingJob save(PickingJob pickingJob);
    public void delete(PickingJob pickingJob);
    public PickingJob fulfill(PickingJob pickingJob, PickingJobDto pickingJobDto);

    /** Dashboard KPI: count of pending picking jobs. */
    long countPending();
}
