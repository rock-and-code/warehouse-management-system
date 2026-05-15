package com.example.warehouseManagement.Services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.StockAdjustment;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Repositories.StockAdjustmentRepository;
import com.example.warehouseManagement.Repositories.StockRepository;

@Service
public class StockAdjustmentServiceImpl implements StockAdjustmentService {

    private final StockAdjustmentRepository repository;
    private final StockRepository stockRepository;

    public StockAdjustmentServiceImpl(StockAdjustmentRepository repository,
                                      StockRepository stockRepository) {
        this.repository = repository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public StockAdjustment apply(StockAdjustment adjustment) {
        switch (adjustment.getType()) {
            case CYCLE_COUNT -> setQtyAt(adjustment, adjustment.getSource(), adjustment.getQty());
            case WRITE_OFF -> decrementAt(adjustment, adjustment.getSource(), adjustment.getQty());
            case DAMAGE, TRANSFER -> {
                decrementAt(adjustment, adjustment.getSource(), adjustment.getQty());
                incrementAt(adjustment, adjustment.getDestination(), adjustment.getQty());
            }
        }
        return repository.save(adjustment);
    }

    @Override
    public Page<StockAdjustment> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // ---- private helpers ----

    /** Set qty_on_hand at (item, section) to an absolute value. Creates a Stock row if none exists. */
    private void setQtyAt(StockAdjustment adj, WarehouseSection section, int qty) {
        Stock stock = stockRepository.findByWarehouseSectionAndItemId(section.getId(), adj.getItem().getId());
        if (stock == null) {
            stockRepository.save(Stock.builder()
                    .item(adj.getItem()).warehouseSection(section).qtyOnHand(qty).build());
        } else {
            stock.setQtyOnHand(qty);
            stockRepository.save(stock);
        }
    }

    /** Decrement the on-hand qty at (item, section). Deletes the row when it reaches zero. */
    private void decrementAt(StockAdjustment adj, WarehouseSection section, int qty) {
        List<Stock> stocks = stockRepository.findByItem(adj.getItem());
        Stock here = stocks.stream()
                .filter(s -> s.getWarehouseSection() != null
                        && s.getWarehouseSection().getId().equals(section.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No stock for item " + adj.getItem().getId()
                                + " in section " + section.getSectionNumber()));
        int remaining = here.getQtyOnHand() - qty;
        if (remaining < 0) {
            throw new IllegalStateException("Cannot adjust below zero for item "
                    + adj.getItem().getId() + " in section " + section.getSectionNumber());
        }
        if (remaining == 0) {
            stockRepository.delete(here);
        } else {
            here.setQtyOnHand(remaining);
            stockRepository.save(here);
        }
    }

    /** Add qty at (item, section). Creates a Stock row if none exists. */
    private void incrementAt(StockAdjustment adj, WarehouseSection section, int qty) {
        Stock stock = stockRepository.findByWarehouseSectionAndItemId(section.getId(), adj.getItem().getId());
        if (stock == null) {
            stockRepository.save(Stock.builder()
                    .item(adj.getItem()).warehouseSection(section).qtyOnHand(qty).build());
        } else {
            stock.setQtyOnHand(stock.getQtyOnHand() + qty);
            stockRepository.save(stock);
        }
    }
}
