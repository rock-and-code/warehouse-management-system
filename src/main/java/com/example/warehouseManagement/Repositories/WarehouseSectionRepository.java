package com.example.warehouseManagement.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.WarehouseSection;
import java.util.List;
import com.example.warehouseManagement.Domains.Stock;


public interface WarehouseSectionRepository extends CrudRepository<WarehouseSection, Long> {
    /**
     * Returns a list of warehouse sections by a given list of stocks
     * Include in at suffix of method: https://stackoverflow.com/questions/60397201/spring-jpa-repository-operator-simple-property-on-jsonobject-requires-a-scalar
     * @param stocks
     * @return
     */
    public List<WarehouseSection> findByStocksIn(List<Stock> stocks);
}
