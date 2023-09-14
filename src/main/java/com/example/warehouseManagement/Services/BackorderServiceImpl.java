package com.example.warehouseManagement.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Repositories.BackorderRepository;

@Service
public class BackorderServiceImpl implements BackorderService {
    private final BackorderRepository backorderRepository;

    public BackorderServiceImpl(BackorderRepository backorderRepository) {
        this.backorderRepository = backorderRepository;
    }

    @Override
    public Iterable<Backorder> findAll() {
        return backorderRepository.findAll();
    }

    @Override
    public List<Backorder> findBySalesOrder(SalesOrder salesOrder) {
        return backorderRepository.findBySalesOrder(salesOrder);
    }

    @Override
    public Backorder save(Backorder backorder) {
        return backorderRepository.save(backorder);
    }

    @Override
    public void delete(Backorder backorder) {
        backorderRepository.delete(backorder);
    }
    
}
