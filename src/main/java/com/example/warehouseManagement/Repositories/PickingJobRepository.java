package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.PickingJob;

public interface PickingJobRepository extends CrudRepository<PickingJob, Long> {
    @Query(value = "SELECT * FROM picking_job pj\n" + //
            "WHERE pj.status = 0 ORDER BY pj.date", nativeQuery = true)
    public List<PickingJob> findAllPending();   
}
