package com.example.warehouseManagement.Repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.ItemCost;

public interface ItemCostRepository extends CrudRepository<ItemCost, Long> {
    @Query(value = """
        SELECT\s
        ic.cost\s
        FROM item_cost ic\s
        WHERE ic.item_id = :itemId\s
        AND ic.start_date <= CURRENT_DATE()\s
        AND ic.end_date >= CURRENT_DATE()\
        """, nativeQuery = true)
    public double findCurrentCostByItemId(@Param("itemId") Long id);

    @Query(value = """
        SELECT * FROM item_cost ic\s
        WHERE ic.item_id = :itemId\s
        AND ic.start_date <= CURRENT_DATE()\s
        AND ic.end_date >= CURRENT_DATE()\
        """, nativeQuery = true)
    public ItemCost findCurrentItemCostByItemId(@Param("itemId") Long id);
}
