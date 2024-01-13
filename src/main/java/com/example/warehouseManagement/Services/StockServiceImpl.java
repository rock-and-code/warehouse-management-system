package com.example.warehouseManagement.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.PutAwayTaskDto;
import com.example.warehouseManagement.Domains.DTOs.PutAwayTasksDtoWrapper;
import com.example.warehouseManagement.Domains.DTOs.StockLevelReportItemDto;
import com.example.warehouseManagement.Domains.DTOs.TopFiveMoversDto;
import com.example.warehouseManagement.Repositories.StockRepository;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

@Service
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final WarehouseSectionRepository warehouseSectionRepository;

    public StockServiceImpl(StockRepository stockRepository, WarehouseSectionRepository warehouseSectionRepository) {
        this.stockRepository = stockRepository;
        this.warehouseSectionRepository = warehouseSectionRepository;
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
            Stock stock = stockRepository.findByWarehouseSectionAndItemId(pickingJobLine.getWarehouseSection().getId(),
                    pickingJobLine.getItem().getId());
            if (stock == null)
                continue; // May happends that a picking job has several lines for same product
            // and since we are using a form to fullfil the job for simulation purposes
            // rathern than scanning an
            // item at the warehouse bin loncation where we can see if a product is
            // available or not
            // we need to check if stock is null since it may be deleted by a previous
            // picked lined and not
            // available for current picking job line
            int remainingBalance = stock.getQtyOnHand() - pickingJobLine.getQtyPicked();
            Long wareHouseSectionId = pickingJobLine.getWarehouseSection().getId();
            Long itemId = pickingJobLine.getItem().getId();
            if (remainingBalance > 0) {
                stockRepository.updateStockByWarehouseSectionAndItemId(remainingBalance, wareHouseSectionId, itemId);
            } else {
                stockRepository.deleteStockByWarehouseSectionAndItemId(wareHouseSectionId, itemId);
            }
        }
    }

    @Override
    public List<Stock> findStocksOnFloor() {
        return stockRepository.findStockOnFloor();
    }

    @Override
    public List<PutAwayTaskDto> generatePutAwayTaskDtoByWarehouseSection(WarehouseSection warehouseSection) {
        List<PutAwayTaskDto> putAwayTaskDtos = new ArrayList<>();
        for (Stock stock : warehouseSection.getStocks()) {
            PutAwayTaskDto putAwayTaskDto = PutAwayTaskDto.builder()
                    .destinationWarehouseSectionId(stock.getWarehouseSection().getId())
                    .qtyToMove(0)
                    .build();
            putAwayTaskDtos.add(putAwayTaskDto);
        }
        return putAwayTaskDtos;
    }

    @Override
    public Optional<Stock> findById(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public void putAwayStocks(PutAwayTasksDtoWrapper wrapper, List<Stock> stocks) {
        for (int i=0; i<wrapper.getPutAwayTasks().size(); i++) {
            PutAwayTaskDto putAwayTaskDto = wrapper.getPutAwayTasks().get(i);
            Stock stock = stocks.get(i);
            if (putAwayTaskDto.getQtyToMove() == 0) continue;
            Long destinationWarehouseSectionId = putAwayTaskDto.getDestinationWarehouseSectionId();
            int qtyMoved = putAwayTaskDto.getQtyToMove();
            Optional<Stock> movedStock = stockRepository.findById(stock.getId());
            if (movedStock.isPresent()) {
                Optional<WarehouseSection> destination = warehouseSectionRepository
                            .findById(destinationWarehouseSectionId);
                if (qtyMoved >= movedStock.get().getQtyOnHand()) {
                    // move all stocks to new destination bin
                    movedStock.get().setWarehouseSection(destination.get());
                    stockRepository.save(movedStock.get());
                } else {
                    // move part of the stocks to a new destination
                    Stock newStock = Stock.builder()
                        .item(movedStock.get().getItem())
                        .qtyOnHand(qtyMoved)
                        .warehouseSection(destination.get())
                        .build();
                    movedStock.get().setQtyOnHand(movedStock.get().getQtyOnHand() - qtyMoved);
                    stockRepository.save(movedStock.get());
                    stockRepository.save(newStock);
                }
            }
        }
    }

    @Override
    public List<Stock> findStocksByWareHouseSectionNumber(String warehouseSectionNumber) {
       return stockRepository.findStocksByWarehouseSectionNumber(warehouseSectionNumber);
    }

}
