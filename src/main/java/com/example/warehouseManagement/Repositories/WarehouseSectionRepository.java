package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.WarehouseSectionDto;


public interface WarehouseSectionRepository extends CrudRepository<WarehouseSection, Long> {
    /**
     * Returns a list of warehouse sections by a given list of stocks
     * Include in at suffix of method: https://stackoverflow.com/questions/60397201/spring-jpa-repository-operator-simple-property-on-jsonobject-requires-a-scalar
     * @param stocks
     * @return
     */
    public List<WarehouseSection> findByStocksIn(List<Stock> stocks);

    @Query(value = "SELECT\n" + //
            "  warehouse_section.section_number AS \"Bin\",\n" + //
            "  s.qty_on_hand AS \"Qty on hand\",\n" + //
            "  item.sku AS \"Sku\"\n" + //
            "FROM STOCK s\n" + //
            "INNER JOIN warehouse_section ON s.warehouse_section_id\n" + //
            "INNER JOIN item on s.item_id  = item.id\n" + //
            "WHERE s.item_id = :itemId\n" + //
            "AND warehouse_section.id= s.warehouse_section_id\n" + //
            "ORDER BY s.qty_on_hand DESC", nativeQuery = true)
    public List<WarehouseSection> findByItemId(@Param("itemId") Long itemId);

    @Query(value = "SELECT TOP 1\n" + //
            "  warehouse_section.id AS \"id\",\n" +
            "  warehouse_section.section_number AS \"warehouseSection\",\n"+
            "  warehouse_section.warehouse_id AS \"warehouseId\"\n" + //
            "FROM STOCK s\n" + //
            "INNER JOIN warehouse_section ON s.warehouse_section_id\n" + //
            "INNER JOIN item on s.item_id  = item.id\n" + //
            "WHERE s.item_id = :itemId\n" + //
            "AND warehouse_section.id= s.warehouse_section_id\n" + //
            "ORDER BY s.qty_on_hand DESC", nativeQuery = true)
    public WarehouseSectionDto findSectionWithHighestQtyOnHandByItemId(@Param("itemId") Long itemId);
    /**
    SELECT
    warehouse_section.section_number AS "Bin",
    s.qty_on_hand AS "Qty on hand",
    item.sku AS "Sku"
    FROM STOCK s
    INNER JOIN warehouse_section ON s.warehouse_section_id
    INNER JOIN item on s.item_id  = item.id
    WHERE s.item_id = 8
    AND warehouse_section.id= s.warehouse_section_id
    ORDER BY s.qty_on_hand DESC
     */
}
