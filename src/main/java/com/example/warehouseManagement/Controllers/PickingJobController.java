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

/**
 * Controller for picking jobs.
 */
@Controller
@RequestMapping(value = "/")
public class PickingJobController {
    public static final String PICKING_JOB_PATH = "picking-jobs";
    public static final String PICKING_JOB_ID_PATH = PICKING_JOB_PATH + "/{pickingJobId}";
    public static final String FULFILL_PICKING_JOB = PICKING_JOB_PATH + "/fulfill-job" + "/{pickingJobId}";
    private final PickingJobService pickingJobService;
    private final WarehouseSectionService warehouseSectionService;
    private final StockService stockService;
    private final InvoiceService invoiceService;

    /**
     * Constructor.
     *
     * @param pickingJobService       the picking job service
     * @param warehouseSectionService the warehouse section service
     * @param stockService            the stock service
     * @param invoiceService          the invoice service
     */
    public PickingJobController(PickingJobService pickingJobService,
            PurchaseOrderService purchaseOrderService,
            WarehouseSectionService warehouseSectionService,
            StockService stockService, InvoiceService invoiceService) {
        this.pickingJobService = pickingJobService;
        this.warehouseSectionService = warehouseSectionService;
        this.stockService = stockService;
        this.invoiceService = invoiceService;
    }

    /**
     * Handles a GET request to fetch all picking jobs.
     *
     * @param model the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @GetMapping(value = PICKING_JOB_PATH)
    public String getAllPickingJobs(Model model) {
        model.addAttribute("title", "Picking Jobs");
        model.addAttribute("pickingJobs", pickingJobService.findAllPending());
        return "pickingJobs/pickingJobs";
    }

    /**
     * Handles a GET request to fetch details of a specific picking job.
     *
     * @param id    the ID of the picking job
     * @param model the Model object to populate with data for the view
     * @return the name of the view template to render
     */
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
            return "redirect:/picking-jobs?notFound";
        }
    }

    /**
     * Handles GET request to initiate the picking process for a specific job.
     *
     * @param id    the ID of the picking job to start picking
     * @param model the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @GetMapping(value = FULFILL_PICKING_JOB)
    public String startPickingProcess(@PathVariable(value = "pickingJobId", required = true) Long id, Model model) {
        // Retrieves the picking job with the specified ID from the database.
        Optional<PickingJob> pickingJob = pickingJobService.findById(id);
        // If the picking job is found, creates a new picking job DTO and populates it
        // with the picking job data. Otherwise, redirects to the picking job list page
        // with an error message.
        if (pickingJob.isPresent()) {
            PickingJobDto pickingJobDto = new PickingJobDto();
            pickingJobDto.setDate(pickingJob.get().getDate());
            pickingJobDto.setSalesOrderId(pickingJob.get().getSalesOrder().getId());
            for (int i = 0; i < pickingJob.get().getPickingJobLines().size(); i++)
                pickingJobDto.getPickingJobDtoLines().add(new PickingJobLineDto());

            // Updates the model with the picking job DTO, the warehouse sections,
            // the title, and the picking job.
            model.addAttribute("pickingJobDto", pickingJobDto);
            model.addAttribute("warehouseSections",
                    warehouseSectionService.findBySalesOrder(pickingJob.get().getSalesOrder()));
            model.addAttribute("title", "Picking Order");
            model.addAttribute("pickingJob", pickingJob.get());
            model.addAttribute("counter", new Counter());
            // Returns the name of the view template for the fulfill picking job form.
            return "pickingJobs/fulfillPickingJobForm";
        } else {
            return "redirect:/picking-jobs?notFound";
        }
    }

    /**
     * Handles a POST request to fulfill a picking job.
     *
     * @param id            the ID of the picking job to fulfill
     * @param pickingJobDto the picking job DTO containing the fulfillment
     *                      information
     * @param model         the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @PostMapping(value = FULFILL_PICKING_JOB)
    public String fulfillPickingJob(@PathVariable(value = "pickingJobId", required = true) Long id,
            @ModelAttribute PickingJobDto pickingJobDto, Model model) {
        // Retrieves the picking job with the specified ID from the database.
        Optional<PickingJob> pickingJob = pickingJobService.findById(id);
        // If the picking job is found, fulfills it with the provided DTO. Otherwise,
        // redirects to the picking job list page with an error message.
        if (pickingJob.isPresent()) {
            // Fulfill the picking job with the provided DTO
            PickingJob updatedPickingJob = pickingJobService.fulfill(pickingJob.get(), pickingJobDto);
            // Update stock levels after fulfilling the picking job
            stockService.pickStock(updatedPickingJob);
            // Create an invoice based on the picking job
            invoiceService.createByPickingJob(pickingJob.get());
            // Redirect to the picking job list page with an error message.
            return "redirect:/picking-jobs";
        } else {
            // Redirect to the picking job list page with an error message.
            return "redirect:/picking-jobs?notFound";
        }
    }
}

/**
 * @GetMapping(value = FULFILL_PICKING_JOB)
 *                   public String startPickingProcess(@PathVariable(value =
 *                   "pickingJobId", required = true) Long id, Model model) {
 *                   Optional<PickingJob> pickingJob =
 *                   pickingJobService.findById(id);
 *                   if (pickingJob.isPresent()) {
 *                   PickingJobDto pickingJobDto = new PickingJobDto();
 *                   pickingJobDto.setDate(pickingJob.get().getDate());
 *                   pickingJobDto.setSalesOrderId(pickingJob.get().getSalesOrder().getId());
 *                   for (int i = 0; i <
 *                   pickingJob.get().getPickingJobLines().size(); i++)
 *                   pickingJobDto.getPickingJobDtoLines().add(new
 *                   PickingJobLineDto());
 *                   model.addAttribute("pickingJobDto", pickingJobDto);
 *                   model.addAttribute("warehouseSections",
 *                   warehouseSectionService.findBySalesOrder(pickingJob.get().getSalesOrder()));
 *                   model.addAttribute("title", "Picking Order");
 *                   model.addAttribute("pickingJob", pickingJob.get());
 *                   model.addAttribute("counter", new Counter());
 *                   return "forms/fulfillPickingJobForm";
 *                   } else {
 *                   return "redirect:/picking-job?notFound";
 *                   }
 *                   }
 */
