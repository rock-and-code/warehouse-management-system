package com.example.warehouseManagement.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.SalesOrderLine;
import com.example.warehouseManagement.Repositories.SaleOrderLineRepository;

@Service
public class SaleOrderLineServiceImpl implements SaleOrderLineService {
    private final SaleOrderLineRepository saleOrderLineRepository;
    


    public SaleOrderLineServiceImpl(SaleOrderLineRepository saleOrderLineRepository) {
        this.saleOrderLineRepository = saleOrderLineRepository;
    }

    @Override
    public List<SalesOrderLine> findBySaleOrder(SalesOrder salesOrder) {
        return saleOrderLineRepository.findBySalesOrder(salesOrder);
    }

    @Override
    public SalesOrderLine save(SalesOrderLine saleOrderLine) {
        return saleOrderLineRepository.save(saleOrderLine);
    }

    @Override
    public void delete(SalesOrderLine saleOrderLine) {
        saleOrderLineRepository.delete(saleOrderLine);
    }

    @Override
    public void deleteAllBySalesOrder(SalesOrder salesOrder) {
        saleOrderLineRepository.deleteAllBySalesOrder(salesOrder);
    }
    
}
