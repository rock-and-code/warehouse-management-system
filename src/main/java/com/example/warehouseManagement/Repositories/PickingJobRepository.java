package com.example.warehouseManagement.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJob.PjStatus;

public interface PickingJobRepository
        extends JpaRepository<PickingJob, Long>,
                JpaSpecificationExecutor<PickingJob> {

    long countByStatus(PjStatus status);
}
