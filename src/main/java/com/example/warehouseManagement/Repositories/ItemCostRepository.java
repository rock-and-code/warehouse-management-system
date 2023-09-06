package com.example.warehouseManagement.Repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.ItemCost;

public interface ItemCostRepository extends CrudRepository<ItemCost, Long> {
    @Query(value = "SELECT \n" +
        "ic.cost \n" +
        "FROM item_cost ic \n" +
        "WHERE ic.item_id = :itemId \n" +
        "AND ic.start_date <= CURRENT_DATE() \n" +
        "AND ic.end_date >= CURRENT_DATE()", nativeQuery = true)
    public double findCurrentCostByItemId(@Param("itemId") Long id);

    @Query(value = "SELECT * FROM item_cost ic \n" +
        "WHERE ic.item_id = :itemId \n" +
        "AND ic.start_date <= CURRENT_DATE() \n" +
        "AND ic.end_date >= CURRENT_DATE()", nativeQuery = true)
    public ItemCost findCurrentItemCostByItemId(@Param("itemId") Long id);
}
