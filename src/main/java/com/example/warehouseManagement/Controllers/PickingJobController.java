package com.example.warehouseManagement.Controllers;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.PickingJobService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Services.WarehouseSectionService;
import com.example.warehouseManagement.Util.Counter;

@Controller
@RequestMapping(value = "/")
public class PickingJobController {
    public static final String PICKING_JOB_PATH = "picking-job";
    public static final String PICKING_JOB_ID_PATH = PICKING_JOB_PATH + "/{pickingJobId}";
    private final PickingJobService pickingJobService;
    private final PurchaseOrderService purchaseOrderService;
    private final ItemService itemService;
    private final WarehouseSectionService warehouseSectionService;
    public PickingJobController(PickingJobService pickingJobService,
            PurchaseOrderService purchaseOrderService, ItemService itemService,
            WarehouseSectionService warehouseSectionService) {
        this.pickingJobService = pickingJobService;
        this.purchaseOrderService = purchaseOrderService;
        this.itemService = itemService;
        this.warehouseSectionService = warehouseSectionService;
    }

    @GetMapping(value = PICKING_JOB_PATH)
    public String getAllPickingJobs(Model model) {
        model.addAttribute("title", "Picking Jobs");
        model.addAttribute("pickingJobs",pickingJobService.findAll());
        return "pickingJobs/pickingJobs";
    }

    @GetMapping(value = PICKING_JOB_ID_PATH)
    public String getPickingJobDetails(@PathVariable(value = "pickingJobId", required = false) Long id, Model model) {
        Optional<PickingJob> pickingJob = pickingJobService.findById(id);
        if (pickingJob.isPresent()) {
            System.out.println(Arrays.toString(warehouseSectionService.findWarehouseSectionSuggestionBySalesOrder(pickingJob.get().getSalesOrder()).toArray()));
            model.addAttribute("warehouseSections", warehouseSectionService.findWarehouseSectionSuggestionBySalesOrder(pickingJob.get().getSalesOrder()));
            model.addAttribute("title", "Picking Job Details");
            model.addAttribute("pickingJob", pickingJob.get());
            model.addAttribute("counter", new Counter());
            return "pickingJobs/pickingJobDetails";
        }
        else {
            return "redirect:/picking-job?notFound";
        }
    }
}

