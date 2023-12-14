package com.example.warehouseManagement.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteDto;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteLineDto;
import com.example.warehouseManagement.Domains.DTOs.WarehouseSectionIdDto;
import com.example.warehouseManagement.Services.GoodsReceiptNoteService;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Services.WarehouseSectionService;
import com.example.warehouseManagement.Util.Counter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller to handle goods receipt note requests.
 */
@Controller
@RequestMapping(value = "/")
public class GoodsReceiptNoteController {
    /**
     * Constants for path definitions.
     */
    private final int NOT_FULFILLED = 0;
    private final int PARTIALLY_FULFILLED = 1;
    public static final String GOODS_RECEIPT_NOTE_PATH = "goods-receipt-notes";
    public static final String NEW_GOODS_RECEIPT_NOTE_PATH = GOODS_RECEIPT_NOTE_PATH + "/new-good-receipt-note";
    public static final String GOODS_RECEIPT_NOTE_ID_PATH = GOODS_RECEIPT_NOTE_PATH + "/{goodsReceiptNoteId}";
    public static final String FULFILL_GOODS_RECEIPT_NOTE = GOODS_RECEIPT_NOTE_PATH + "/fulfill"
            + "/{goodsReceiptNoteId}";
    /**
     * Dependencies.
     */
    private final GoodsReceiptNoteService goodsReceiptNoteService;
    private final PurchaseOrderService purchaseOrderService;
    private final ItemService itemService;
    private final WarehouseSectionService warehouseSectionService;

    /**
     * Constructor for dependency injection.
     *
     * @param goodsReceiptNoteService the GoodsReceiptNoteService dependency
     * @param purchaseOrderService    the PurchaseOrderService dependency
     * @param itemService             the ItemService dependency
     * @param warehouseSectionService the WarehouseSectionService dependency
     */
    public GoodsReceiptNoteController(GoodsReceiptNoteService goodsReceiptNoteService,
            PurchaseOrderService purchaseOrderService, ItemService itemService,
            WarehouseSectionService warehouseSectionService) {
        this.goodsReceiptNoteService = goodsReceiptNoteService;
        this.purchaseOrderService = purchaseOrderService;
        this.itemService = itemService;
        this.warehouseSectionService = warehouseSectionService;
    }

    /**
     * Handles GET request for creating a new goods receipt note.
     *
     * @param goodsReceiptNote the GoodsReceiptNote object to be bound to the model
     * @param model            the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @GetMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH)
    public String newGoodsReceiptNote(@ModelAttribute GoodsReceiptNote goodsReceiptNote, Model model) {
        model.addAttribute("goodsReceiptNote", new GoodsReceiptNote());
        model.addAttribute("title", "New Goods Receipt Note");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "goodsReceiptNotes/goodsReceiptNoteForm";
    }

    /**
     * Handles POST request for adding a new goods receipt note line.
     *
     * @param goodsReceiptNote the GoodsReceiptNote object to be bound to the model
     * @param request          the HttpServletRequest object to access request
     *                         parameters
     * @param model            the Model object to populate with data for the view
     * @return the name of the view template to render
     */

    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "addRow")
    public String addGoodReceiptNoteLine(@ModelAttribute GoodsReceiptNote goodsReceiptNote,
            HttpServletRequest request, Model model) {
        // Adding new goods receipt note line to current goods receipt note
        goodsReceiptNote.getGoodsReceiptNoteLines().add(new GoodsReceiptNoteLine());
        model.addAttribute("title", "New Goods Receipt Note");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "goodsReceiptNotes/goodsReceiptNoteForm";
    }

    /**
     * Handles POST request for removing a goods receipt note line.
     *
     * @param goodsReceiptNote the GoodsReceiptNote object to be bound to the model
     * @param request          the HttpServletRequest object to access request
     *                         parameters
     * @param model            the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "removeRow")
    public String removeGoodsReceiptNoteLine(@ModelAttribute GoodsReceiptNote goodsReceiptNote,
            HttpServletRequest request, Model model) {

        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        // Removes the goods receipt note line at the specified row index.
        goodsReceiptNote.getGoodsReceiptNoteLines().remove(rowId);
        // Updates the model with the updated goods receipt note object and the updated
        // list of purchase orders and items.
        model.addAttribute("title", "New Goods Receipt Note");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        // Returns the view template name for the goods receipt note form.
        return "goodsReceiptNotes/goodsReceiptNoteForm";
    }

    /**
     * Handles POST request for saving a new goods receipt note.
     *
     * @param goodsReceiptNote the GoodsReceiptNote object to be saved
     * @return the name of the view template to render
     */
    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "save")
    public String saveNewGoodsReceiptNote(@ModelAttribute GoodsReceiptNote goodsReceiptNote) {
        // Saves the new goods receipt note to the database.
        goodsReceiptNoteService.save(goodsReceiptNote);
        // Redirects to the goods receipt note list page with a success message.
        return "redirect:/goods-receipt-notes?added";
    }

    /**
     * Handling GET request for retrieving all pending goods receipt notes
     * 
     * @param model the Model object to populate with data for the view
     * @return the new of the view template to render
     */
    @GetMapping(value = GOODS_RECEIPT_NOTE_PATH)
    public String getAllPendingGoodsReceiptNotes(Model model) {
        // Retrieves a list of all pending goods receipt notes from the database.
        model.addAttribute("title", "Goods Receipt Notes");
        // Updates the model with the list of pending goods receipt notes.
        model.addAttribute("goodsReceiptNotes", goodsReceiptNoteService.findAllPending());
        // Returns the name of the view template for the goods receipt note list page.
        return "goodsReceiptNotes/goodsReceiptNotes";
    }

    /**
     * Handling GET request for retrieving details of a specific goods receipt note
     * 
     * @param id    the goods receipt note id
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of goods
     *         receipt notes if the goods receipt note is not found or cannot be
     *         deleted
     */
    @GetMapping(value = GOODS_RECEIPT_NOTE_ID_PATH)
    public String getGoodsReceiptNoteDetails(@PathVariable(value = "goodsReceiptNoteId", required = false) Long id,
            Model model) {
        // Retrieves the goods receipt note with the specified ID from the database.
        Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findById(id);
        // If the goods receipt note is found, updates the model with the goods receipt
        // note object. Otherwise, redirects to the goods receipt note list page with
        // an error message.
        if (goodsReceiptNote.isPresent()) {
            model.addAttribute("title", "Goods Receipt Note Details");
            model.addAttribute("goodsReceiptNote", goodsReceiptNote.get());
            model.addAttribute("counter", new Counter());
            return "goodsReceiptNotes/goodsReceiptNoteDetails";
        } else {
            return "redirect:/goods-receipt-notes?notFound";
        }
    }

    /**
     * Handling GET request for starting to receive a shipment
     * 
     * @param id    the goods reciept note id
     * @param model the Mode to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of goods
     *         receipt notes if the goods receipt note is not found or cannot be
     *         deleted
     */
    @GetMapping(value = FULFILL_GOODS_RECEIPT_NOTE)
    public String startReceiveShipment(@PathVariable(value = "goodsReceiptNoteId", required = true) Long id,
            Model model) {
        // Retrieves the goods receipt note with the specified ID from the database.
        Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findById(id);
        if (goodsReceiptNote.isPresent()) {
            // To simulates the put away process after receiving a shipment
            // No practical since warehouse staff needs to make sure the bins is empty to
            // move stocks in

            // If the goods receipt note is found, creates a new goods receipt note DTO
            // and populates it with the goods receipt note data. Otherwise, redirects
            // to the goods receipt note list page with an error message.
            GoodsReceiptNoteDto goodsReceiptNoteDto = GoodsReceiptNoteDto.builder()
                    .date(goodsReceiptNote.get().getDate())
                    .purchaseOrderId(goodsReceiptNote.get().getPurchaseOrder().getId()).build();
            List<WarehouseSectionIdDto> selectedWarehouseSections = new ArrayList<>();
            // Creates a list of selected warehouse section IDs and populates it with
            // empty warehouse section IDs.
            for (int i = 0; i < goodsReceiptNote.get().getGoodsReceiptNoteLines().size(); i++) {
                selectedWarehouseSections.add(new WarehouseSectionIdDto());
                goodsReceiptNoteDto.getGoodsReceiptNoteLines().add(new GoodsReceiptNoteLineDto());
            }
            // Updates the model with the goods receipt note DTO, the list of selected
            // warehouse section IDs, the warehouse sections, and the title.
            model.addAttribute("goodsReceiptNoteDto", goodsReceiptNoteDto);
            model.addAttribute("selectedWarehouseSections", selectedWarehouseSections);
            model.addAttribute("goodsReceiptNote", goodsReceiptNote.get());
            model.addAttribute("warehouseSections",
                    warehouseSectionService.findAll());
            model.addAttribute("title", "Receive Shipment");
            model.addAttribute("counter", new Counter());
            // Returns the name of the view template for the fulfill goods receipt note
            // form.
            return "goodsReceiptNotes/fulfillGoodsReceiptNoteForm";
        } else {
            return "redirect:/goods-receipt-notes?notFound";
        }
    }

    /**
     * Handles POST request for fulfilling a goods receipt note.
     *
     * @param id                  the ID of the goods receipt note to fulfill
     * @param goodsReceiptNoteDto the goods receipt note DTO containing the
     *                            fulfillment information
     * @param model               the Model object to populate with data for the
     *                            view
     * @return the name of the view template to render
     */
    @PostMapping(value = FULFILL_GOODS_RECEIPT_NOTE)
    public String fulfillGoodsReceiptNote(@PathVariable(value = "goodsReceiptNoteId", required = true) Long id,
            @ModelAttribute GoodsReceiptNoteDto goodsReceiptNoteDto, Model model) {
        // Retrieves the goods receipt note with the specified ID from the database.
        Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findById(id);
        if (goodsReceiptNote.isPresent()) {
            // If the goods receipt note is found, fulfills it with the provided DTO.
            // Otherwise, redirects to the goods receipt note list page with an error
            // message.
            int fulfillment = goodsReceiptNoteService.fulfill(goodsReceiptNote.get(), goodsReceiptNoteDto);
            if (fulfillment == NOT_FULFILLED)
                return "redirect:/goods-receipt-notes?notFulfilled";
            else if (fulfillment == PARTIALLY_FULFILLED)
                return "redirect:/goods-receipt-notes?partiallyFulfilled";
            else
                return "redirect:/goods-receipt-notes?fulfilled";
        } else {
            return "redirect:/goods-receipt-notes?notFound";
        }

    }

    // NO USE CASE FOR DELETING A GOODS RECEIPT NOT SINCE IT IS THE PROOF THAT GOODS
    // HAVE BEEN RECEIVED FROM A VENDOR AND
    // STORED IN THE WAREHOUSE

    // @PostMapping(value = GOODS_RECEIPT_NOTE_ID_PATH)
    // public String deleteGoodsReceiptNotes(@PathVariable(name =
    // "goodsReceiptNoteId", required = false) Long id, Model model) {
    // Optional<GoodsReceiptNote> goodsReceiptNote =
    // goodsReceiptNoteService.findById(id);
    // if (goodsReceiptNote.isPresent()) {
    // goodsReceiptNoteService.delete(goodsReceiptNote.get());
    // return "redirect:/goodsReceiptNotes?deleted";
    // }
    // else {
    // return "redirect:/goodsReceiptNotes?notFound";
    // }
    // }

}
