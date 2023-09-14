package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.DTOs.PickingJobDto;
import com.example.warehouseManagement.Domains.DTOs.PickingJobLineDto;
import com.example.warehouseManagement.Services.InvoiceService;
import com.example.warehouseManagement.Services.PickingJobService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Services.StockService;
import com.example.warehouseManagement.Services.WarehouseSectionService;
import com.example.warehouseManagement.Util.Counter;

@Controller
@RequestMapping(value = "/")
public class PickingJobController {
    public static final String PICKING_JOB_PATH = "picking-job";
    public static final String PICKING_JOB_ID_PATH = PICKING_JOB_PATH + "/{pickingJobId}";
    public static final String FULFILL_PICKING_JOB = PICKING_JOB_PATH + "/fulfill-job" + "/{pickingJobId}";
    private final PickingJobService pickingJobService;
    private final WarehouseSectionService warehouseSectionService;
    private final StockService stockService;
    private final InvoiceService invoiceService;

    public PickingJobController(PickingJobService pickingJobService,
            PurchaseOrderService purchaseOrderService,
            WarehouseSectionService warehouseSectionService,
            StockService stockService, InvoiceService invoiceService) {
        this.pickingJobService = pickingJobService;
        this.warehouseSectionService = warehouseSectionService;
        this.stockService = stockService;
        this.invoiceService = invoiceService;
    }

    @GetMapping(value = PICKING_JOB_PATH)
    public String getAllPickingJobs(Model model) {
        model.addAttribute("title", "Picking Jobs");
        model.addAttribute("pickingJobs", pickingJobService.findAllPending());
        return "pickingJobs/pickingJobs";
    }

    @GetMapping(value = PICKING_JOB_ID_PATH)
    public String getPickingJobDetails(@PathVariable(value = "pickingJobId", required = false) Long id, Model model) {
        Optional<PickingJob> pickingJob = pickingJobService.findById(id);
        if (pickingJob.isPresent()) {
            model.addAttribute("warehouseSections", warehouseSectionService
                    .findWarehouseSectionSuggestionBySalesOrder(pickingJob.get().getSalesOrder()));
            model.addAttribute("title", "Picking Job Details");
            model.addAttribute("pickingJob", pickingJob.get());
            model.addAttribute("counter", new Counter());
            return "pickingJobs/pickingJobDetails";
        } else {
            return "redirect:/picking-job?notFound";
        }
    }

    @GetMapping(value = FULFILL_PICKING_JOB)
    public String startPickingProcess(@PathVariable(value = "pickingJobId", required = true) Long id, Model model) {
        Optional<PickingJob> pickingJob = pickingJobService.findById(id);
        if (pickingJob.isPresent()) {
            PickingJobDto pickingJobDto = new PickingJobDto();
            pickingJobDto.setDate(pickingJob.get().getDate());
            pickingJobDto.setSalesOrderId(pickingJob.get().getSalesOrder().getId());
            for (int i = 0; i < pickingJob.get().getPickingJobLines().size(); i++)
                pickingJobDto.getPickingJobDtoLines().add(new PickingJobLineDto());
            model.addAttribute("pickingJobDto", pickingJobDto);
            model.addAttribute("warehouseSections",
                    warehouseSectionService.findBySalesOrder(pickingJob.get().getSalesOrder()));
            model.addAttribute("title", "Picking Order");
            model.addAttribute("pickingJob", pickingJob.get());
            model.addAttribute("counter", new Counter());
            return "forms/fulfillPickingJobForm";
        } else {
            return "redirect:/picking-job?notFound";
        }
    }

    @PostMapping(value = FULFILL_PICKING_JOB)
    public String fulfillPickingJob(@PathVariable(value = "pickingJobId", required = true) Long id, 
        @ModelAttribute PickingJobDto pickingJobDto, Model model) {
        Optional<PickingJob> pickingJob = pickingJobService.findById(id); 
        if (pickingJob.isPresent()) {
            //FULFILL ORDER
            PickingJob updatedPickingJob = pickingJobService.fulfill(pickingJob.get(), pickingJobDto);
            //UPDATES STOCK LEVES AFTER FULFILLING PICKING JOB
            stockService.pickStock(updatedPickingJob);
            //CREATES A INVOICE BY PICKING JOB
            invoiceService.createByPickingJob(pickingJob.get());
            return "redirect:/picking-job";
        } else {
            return "redirect:/picking-job?notFound";
        }
    }
}


/**
@GetMapping(value = FULFILL_PICKING_JOB)
    public String startPickingProcess(@PathVariable(value = "pickingJobId", required = true) Long id, Model model) {
        Optional<PickingJob> pickingJob = pickingJobService.findById(id);
        if (pickingJob.isPresent()) {
            PickingJobDto pickingJobDto = new PickingJobDto();
            pickingJobDto.setDate(pickingJob.get().getDate());
            pickingJobDto.setSalesOrderId(pickingJob.get().getSalesOrder().getId());
            for (int i = 0; i < pickingJob.get().getPickingJobLines().size(); i++)
                pickingJobDto.getPickingJobDtoLines().add(new PickingJobLineDto());
            model.addAttribute("pickingJobDto", pickingJobDto);
            model.addAttribute("warehouseSections",
                    warehouseSectionService.findBySalesOrder(pickingJob.get().getSalesOrder()));
            model.addAttribute("title", "Picking Order");
            model.addAttribute("pickingJob", pickingJob.get());
            model.addAttribute("counter", new Counter());
            return "forms/fulfillPickingJobForm";
        } else {
            return "redirect:/picking-job?notFound";
        }
    }
 */
