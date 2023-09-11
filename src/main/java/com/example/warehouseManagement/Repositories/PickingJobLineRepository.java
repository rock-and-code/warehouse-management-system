package com.example.warehouseManagement.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.PickingJobLine;

public interface PickingJobLineRepository extends CrudRepository<PickingJobLine, Long> {
    /**
    SELECT
    pj.date AS "Date",
    pj.sales_order_id AS "Sales Order"
    FROM picking_job pj
    INNER JOIN picking_job_line ON picking_job_id = pj.id
    WHERE pj.id = 3
     */
}
