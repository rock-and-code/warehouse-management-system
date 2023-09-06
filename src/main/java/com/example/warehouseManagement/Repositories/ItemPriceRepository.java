package com.example.warehouseManagement.Repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.ItemPrice;

public interface ItemPriceRepository extends CrudRepository<ItemPrice, Long>{
    
    @Query(value = "SELECT \n" +
        "ip.price \n" +
        "FROM item_price ip \n" +
        "WHERE ip.item_id = :itemId \n" +
        "AND ip.start_date <= CURRENT_DATE() \n" +
        "AND ip.end_date >= CURRENT_DATE()", nativeQuery = true)
    public double findCurrentPriceByItemId(@Param("itemId") Long id);

    @Query(value = "SELECT * FROM item_price ip \n" +
        "WHERE ip.item_id = :itemId \n" +
        "AND ip.start_date <= CURRENT_DATE() \n" +
        "AND ip.end_date >= CURRENT_DATE()", nativeQuery = true)
    public ItemPrice findCurrentItemPriceByItemId(@Param("itemId") Long id);
    
}

/**
 * SELECT
  ip.price
FROM item_price ip
WHERE ip.item_id = 7
AND ip.start_date <= CURRENT_DATE()
AND ip.end_date >= CURRENT_DATE()
 */