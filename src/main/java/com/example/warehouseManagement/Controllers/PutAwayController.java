package com.example.warehouseManagement.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.PutAwayTasksDtoWrapper;
import com.example.warehouseManagement.Domains.DTOs.SearchWarehouseSectionDto;
import com.example.warehouseManagement.Services.StockService;
import com.example.warehouseManagement.Services.WarehouseSectionService;

@Controller
@RequestMapping(value = "/")
public class PutAwayController {
    /**
     * Constants for path definitions.
     */
    public static final String PUT_AWAY_PATH = "put-aways";
    public static final String PUT_AWAY_BY_SPECIFIC_WAREHOUSE_SECTION_PATH = PUT_AWAY_PATH + "/{warehouseSectionId}";
    public static final String SEARCH_WAREHOUSE_SECTION = PUT_AWAY_PATH + "/search-warehouse-section";
    public static final String PUT_AWAY_ITEMS_FROM_FLOOR_PATH = PUT_AWAY_PATH + "/floor";
    public static final String FLOOR = "00-00-0-0";
    /**
     * Dependencies.
     */
    private final StockService stockService;
    private final WarehouseSectionService warehouseSectionService;

    /**
     * Constructor for dependency injection.
     *
     * @param stockService            the StockService dependency
     * @param warehouseSectionService the WarehouseSectionService dependency
     */
    public PutAwayController(StockService stockService,
            WarehouseSectionService warehouseSectionService) {
        this.stockService = stockService;
        this.warehouseSectionService = warehouseSectionService;
    }

    @GetMapping(value = PUT_AWAY_ITEMS_FROM_FLOOR_PATH)
    public String putAwayTaskOnFloor(Model model) {
        Optional<WarehouseSection> floor = warehouseSectionService.findWarehouseSectionBySectionNumber(FLOOR);
        PutAwayTasksDtoWrapper wrapper = PutAwayTasksDtoWrapper.builder()
                .putAwayTasks(stockService.generatePutAwayTaskDtoByWarehouseSection(floor.get())).build();
        model.addAttribute("stocks", stockService.findStocksOnFloor());
        model.addAttribute("wrapper", wrapper);
        model.addAttribute("warehouseSections", warehouseSectionService.findAll());
        model.addAttribute("title", "Put Aways Tasks On Floor");
        return "putAwayTasks/putAwayTasksOnFloor";
    }

    @PostMapping(value = PUT_AWAY_ITEMS_FROM_FLOOR_PATH)
    public String fulfillPutAwayTask(@ModelAttribute PutAwayTasksDtoWrapper wrapper, Model model) {
        List<Stock> stocks = stockService.findStocksOnFloor();
        if (wrapper != null) {
            stockService.putAwayStocks(wrapper, stocks);
            return "redirect:/put-aways/floor?putAway";
        } else {
            return "redirect:/put-aways/floor?notFound";
        }
    }

    @GetMapping(value = SEARCH_WAREHOUSE_SECTION)
    public String searchWarehouseSection(Model model) {
        model.addAttribute("searchWarehouseSectionDto", new SearchWarehouseSectionDto());
        model.addAttribute("title", "Search Warehouse Section");
        return "putAwayTasks/searchWarehouseSectionForm";
    }

    @PostMapping(value = SEARCH_WAREHOUSE_SECTION)
    public String putAwayFromSpecificWarehouseSection(
            @ModelAttribute SearchWarehouseSectionDto searchWarehouseSectionDto,
            Model model) {
        String warehouseSectionNumber = searchWarehouseSectionDto.getWarehouseSectionNumber();
        Optional<WarehouseSection> targetWarehouseSection = warehouseSectionService
                .findWarehouseSectionBySectionNumber(warehouseSectionNumber);
        if (targetWarehouseSection.isPresent()) {
            PutAwayTasksDtoWrapper wrapper = PutAwayTasksDtoWrapper.builder()
                    .putAwayTasks(stockService.generatePutAwayTaskDtoByWarehouseSection(targetWarehouseSection.get()))
                    .build();
            model.addAttribute("stocks", stockService.findStocksByWareHouseSectionNumber(warehouseSectionNumber));
            model.addAttribute("warehouseSectionId", targetWarehouseSection.get().getId());
            model.addAttribute("wrapper", wrapper);
            model.addAttribute("warehouseSections", warehouseSectionService.findAll());
            model.addAttribute("title", "Put Aways Tasks");
            return "putAwayTasks/putAwayTasks";
        } else {
            return "redirect:/put-aways/search-warehouse-section?notFound";
        }
    }

    @PostMapping(value = PUT_AWAY_BY_SPECIFIC_WAREHOUSE_SECTION_PATH)
    public String fulfillPutAwayTaskFromSpecificWarehouseSection(@PathVariable Long warehouseSectionId,
            @ModelAttribute PutAwayTasksDtoWrapper wrapper,
            Model model) {
        Optional<WarehouseSection> warehouseSection = warehouseSectionService.findById(warehouseSectionId);
        if (warehouseSection.isPresent()) {
            // System.out.printf("Warehouse Section Id: %d| Number: %s\n", warehouseSection.get().getId(), warehouseSection.get().getSectionNumber());
            List<Stock> stocks = stockService.findStocksByWareHouseSectionNumber(warehouseSection.get().getSectionNumber());
            if (wrapper != null) {
                stockService.putAwayStocks(wrapper, stocks);
                return "redirect:/put-aways/search-warehouse-section?putAway";
            } else {
                return "redirect:/put-aways/search-warehouse-section?notFound";
            }
        } else {
            return "redirect:/put-aways/search-warehouse-section?notFound";
        }

    }

}
