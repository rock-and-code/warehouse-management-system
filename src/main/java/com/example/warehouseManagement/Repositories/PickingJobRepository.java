package com.example.warehouseManagement.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.PickingJob;

public interface PickingJobRepository extends CrudRepository<PickingJob, Long> {
    
}
