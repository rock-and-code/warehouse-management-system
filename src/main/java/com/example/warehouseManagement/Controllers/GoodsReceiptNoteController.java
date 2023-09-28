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

@Controller
@RequestMapping(value = "/")
public class GoodsReceiptNoteController {
    // Constants for path definitions
    public static final String GOODS_RECEIPT_NOTE_PATH = "goods-receipt-note";
    public static final String NEW_GOODS_RECEIPT_NOTE_PATH = GOODS_RECEIPT_NOTE_PATH + "/new-good-receipt-note";
    public static final String GOODS_RECEIPT_NOTE_ID_PATH = GOODS_RECEIPT_NOTE_PATH + "/{goodsReceiptNoteId}";
    public static final String FULFILL_GOODS_RECEIPT_NOTE = GOODS_RECEIPT_NOTE_PATH + "/fulfill"
            + "/{goodsReceiptNoteId}";
    private final GoodsReceiptNoteService goodsReceiptNoteService;
    private final PurchaseOrderService purchaseOrderService;
    private final ItemService itemService;
    private final WarehouseSectionService warehouseSectionService;

     // Constructor for dependency injection
    public GoodsReceiptNoteController(GoodsReceiptNoteService goodsReceiptNoteService,
            PurchaseOrderService purchaseOrderService, ItemService itemService,
            WarehouseSectionService warehouseSectionService) {
        this.goodsReceiptNoteService = goodsReceiptNoteService;
        this.purchaseOrderService = purchaseOrderService;
        this.itemService = itemService;
        this.warehouseSectionService = warehouseSectionService;
    }

     // Handling GET request for creating a new goods receipt note
    @GetMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH)
    public String newGoodsReceiptNote(@ModelAttribute GoodsReceiptNote goodsReceiptNote, Model model) {
        model.addAttribute("goodsReceiptNote", new GoodsReceiptNote());
        model.addAttribute("title", "New Goods Receipt Note");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/goodsReceiptNoteForm";
    }

    // Handling POST request for adding a new goods receipt note line
    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "addRow")
    public String addGoodReceiptNoteLine(@ModelAttribute GoodsReceiptNote goodsReceiptNote,
            HttpServletRequest request, Model model) {
        // Adding new goods receipt note line to current goods receipt note
        goodsReceiptNote.getGoodsReceiptNoteLines().add(new GoodsReceiptNoteLine());
        model.addAttribute("title", "New Goods Receipt Note");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/goodsReceiptNoteForm";
    }

    // Handling POST request for removing a goods receipt note line
    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "removeRow")
    public String removeGoodsReceiptNoteLine(@ModelAttribute GoodsReceiptNote goodsReceiptNote,
            HttpServletRequest request, Model model) {
        // newSalesOrderDto.addOrderLine();
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        // Removing the selected goods receipt note line
        goodsReceiptNote.getGoodsReceiptNoteLines().remove(rowId);
        model.addAttribute("title", "New Goods Receipt Note");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/goodsReceiptNoteForm";
    }

    // Handling POST request for saving a new goods receipt note
    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "save")
    public String saveNewGoodsReceiptNote(@ModelAttribute GoodsReceiptNote goodsReceiptNote) {
        goodsReceiptNoteService.save(goodsReceiptNote);
        return "redirect:/goods-receipt-note?added";
    }

    // Handling GET request for retrieving all pending goods receipt notes
    @GetMapping(value = GOODS_RECEIPT_NOTE_PATH)
    public String getAllPendingGoodsReceiptNotes(Model model) {
        model.addAttribute("title", "Goods Receipt Notes");
        model.addAttribute("goodsReceiptNotes", goodsReceiptNoteService.findAllPending());
        return "goodsReceiptNotes/goodsReceiptNotes";
    }

    // Handling GET request for retrieving details of a specific goods receipt note
    @GetMapping(value = GOODS_RECEIPT_NOTE_ID_PATH)
    public String getGoodsReceiptNoteDetails(@PathVariable(value = "goodsReceiptNoteId", required = false) Long id,
            Model model) {
        Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findById(id);
        if (goodsReceiptNote.isPresent()) {
            model.addAttribute("title", "Goods Receipt Note Details");
            model.addAttribute("goodsReceiptNote", goodsReceiptNote.get());
            model.addAttribute("counter", new Counter());
            return "goodsReceiptNotes/goodsReceiptNoteDetails";
        } else {
            return "redirect:/goods-receipt-note?notFound";
        }
    }

    // Handling GET request for starting to receive a shipment
    @GetMapping(value = FULFILL_GOODS_RECEIPT_NOTE)
    public String startReceiveShipment(@PathVariable(value = "goodsReceiptNoteId", required = true) Long id,
            Model model) {
        Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findById(id);
        if (goodsReceiptNote.isPresent()) {
            // To simulates the put away process after receiving a shipment
            // No practical since warehouse staff needs to make sure the bins is empty to
            // move stocks in
            GoodsReceiptNoteDto goodsReceiptNoteDto = GoodsReceiptNoteDto.builder()
                    .date(goodsReceiptNote.get().getDate())
                    .purchaseOrderId(goodsReceiptNote.get().getPurchaseOrder().getId()).build();
            List<WarehouseSectionIdDto> selectedWarehouseSections = new ArrayList<>();
            for (int i = 0; i < goodsReceiptNote.get().getGoodsReceiptNoteLines().size(); i++) {
                selectedWarehouseSections.add(new WarehouseSectionIdDto());
                goodsReceiptNoteDto.getGoodsReceiptNoteLines().add(new GoodsReceiptNoteLineDto());
            }
            model.addAttribute("goodsReceiptNoteDto", goodsReceiptNoteDto);
            model.addAttribute("selectedWarehouseSections", selectedWarehouseSections);
            model.addAttribute("goodsReceiptNote", goodsReceiptNote.get());
            model.addAttribute("warehouseSections",
                    warehouseSectionService.findAll());
            model.addAttribute("title", "Receive Shipment");
            model.addAttribute("counter", new Counter());
            return "forms/fulfillGoodsReceiptNoteForm";
        } else {
            return "redirect:/goods-receipt-note?notFound";
        }
    }

    // Handling POST request for fulfilling a goods receipt note
    @PostMapping(value = FULFILL_GOODS_RECEIPT_NOTE)
    public String fulfillGoodsReceiptNote(@PathVariable(value = "goodsReceiptNoteId", required = true) Long id,
            @ModelAttribute GoodsReceiptNoteDto goodsReceiptNoteDto, Model model) {
        Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findById(id);
        if (goodsReceiptNote.isPresent()) {
             // Fulfill the goods receipt note with the provided DTO
            goodsReceiptNoteService.fulfill(goodsReceiptNote.get(), goodsReceiptNoteDto);
            return "redirect:/goods-receipt-note/added";
        } else {
            // Redirect to a not found page if the goods receipt note is not found
            return "redirect:/goods-receipt-note?notFound";
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
