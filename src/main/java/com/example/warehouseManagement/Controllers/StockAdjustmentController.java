package com.example.warehouseManagement.Controllers;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.StockAdjustment;
import com.example.warehouseManagement.Domains.StockAdjustment.AdjustmentType;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.StockAdjustmentService;
import com.example.warehouseManagement.Services.WarehouseSectionService;

@Controller
@RequestMapping("/stock-adjustments")
public class StockAdjustmentController {

    private static final String NEW_PATH = "/new";

    private final StockAdjustmentService stockAdjustmentService;
    private final ItemService itemService;
    private final WarehouseSectionService warehouseSectionService;

    public StockAdjustmentController(StockAdjustmentService stockAdjustmentService,
                                     ItemService itemService,
                                     WarehouseSectionService warehouseSectionService) {
        this.stockAdjustmentService = stockAdjustmentService;
        this.itemService = itemService;
        this.warehouseSectionService = warehouseSectionService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 25, sort = "adjustmentDate", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {
        model.addAttribute("title", "Stock Adjustments");
        model.addAttribute("page", stockAdjustmentService.findAll(pageable));
        return "stockAdjustments/stockAdjustments";
    }

    @GetMapping(NEW_PATH)
    public String newForm(Model model) {
        model.addAttribute("title", "New Adjustment");
        model.addAttribute("stockAdjustment",
                StockAdjustment.builder().adjustmentDate(LocalDate.now()).type(AdjustmentType.CYCLE_COUNT).build());
        model.addAttribute("items", itemService.findAll());
        model.addAttribute("sections", warehouseSectionService.findAll());
        model.addAttribute("types", AdjustmentType.values());
        return "stockAdjustments/newStockAdjustmentForm";
    }

    @PostMapping(NEW_PATH)
    public String save(@ModelAttribute StockAdjustment stockAdjustment, Model model) {
        // Re-hydrate FK fields the form posts as id-only.
        Item item = itemService.findById(stockAdjustment.getItem().getId()).orElse(null);
        WarehouseSection source = warehouseSectionService.findById(stockAdjustment.getSource().getId()).orElse(null);
        WarehouseSection destination = (stockAdjustment.getDestination() != null
                                        && stockAdjustment.getDestination().getId() != null)
                ? warehouseSectionService.findById(stockAdjustment.getDestination().getId()).orElse(null)
                : null;
        if (item == null || source == null) {
            return "redirect:/stock-adjustments/new?invalid";
        }
        stockAdjustment.setItem(item);
        stockAdjustment.setSource(source);
        stockAdjustment.setDestination(destination);
        if (stockAdjustment.getAdjustmentDate() == null) {
            stockAdjustment.setAdjustmentDate(LocalDate.now());
        }
        try {
            stockAdjustmentService.apply(stockAdjustment);
        } catch (IllegalStateException e) {
            return "redirect:/stock-adjustments/new?failed";
        }
        return "redirect:/stock-adjustments?applied";
    }
}
