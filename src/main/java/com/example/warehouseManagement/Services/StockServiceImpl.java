package com.example.warehouseManagement.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.DTOs.StockLevelReportItemDto;
import com.example.warehouseManagement.Domains.DTOs.TopFiveMoversDto;
import com.example.warehouseManagement.Repositories.StockRepository;

@Service
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<TopFiveMoversDto> getTopFiveMovers() {
        return stockRepository.getTopFiveMovers();
    }

    @Override
    public List<StockLevelReportItemDto> findStockReportsItemsByVendorId(Long vendorId) {
        return stockRepository.findStockReportsItemsByVendorId(vendorId);
    }

    @Override
    public void pickStock(PickingJob pickingJob) {
        for (PickingJobLine pickingJobLine : pickingJob.getPickingJobLines()) {
            Stock stock = stockRepository.findByWarehouseSectionAndItemId(pickingJobLine.getWarehouseSection().getId(), pickingJobLine.getItem().getId());
            if (stock == null)
                continue; // May happends that a picking job has several lines for same product 
                // and since we are using a form to fullfil the job for simulation purposes rathern than scanning an 
                // item at the warehouse bin loncation where we can see if a product is available or not
                // we need to check if stock is null since it may be deleted by a previous picked lined and not 
                // available for current picking job line
            int remainingBalance = stock.getQtyOnHand() - pickingJobLine.getQtyPicked();
            Long wareHouseSectionId = pickingJobLine.getWarehouseSection().getId();
            Long itemId = pickingJobLine.getItem().getId();
            if (remainingBalance > 0) {
                stockRepository.updateStockByWarehouseSectionAndItemId(remainingBalance, wareHouseSectionId, itemId);
            }
            else {
                stockRepository.deleteStockByWarehouseSectionAndItemId(wareHouseSectionId, itemId);
            }
        }
    }

    
    
}
