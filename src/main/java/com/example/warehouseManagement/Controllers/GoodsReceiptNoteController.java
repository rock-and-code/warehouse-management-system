package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrder.PoStatus;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteDto;
import com.example.warehouseManagement.Domains.DTOs.PurchaseOrderNumberDto;
import com.example.warehouseManagement.Services.GoodsReceiptNoteService;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Services.WarehouseSectionService;
import com.example.warehouseManagement.Util.Counter;

/**
 * Controller to handle goods receipt note requests.
 */
@Controller
@RequestMapping(value = "/")
public class GoodsReceiptNoteController {
    /**
     * Constants for path definitions.
     */
    public static final String GOODS_RECEIPT_NOTE_PATH = "goods-receipt-notes";
    public static final String GOODS_RECEIPT_NOTE_ID_PATH = GOODS_RECEIPT_NOTE_PATH + "/{goodsReceiptNoteId}";
    public static final String FULFILLED_GOODS_RECEIPT_NOTE_PATH = GOODS_RECEIPT_NOTE_PATH + "/fulfilled-goods-receipt-notes";
    public static final String PENDING_GOODS_RECEIPT_NOTE_PATH = GOODS_RECEIPT_NOTE_PATH + "/pending-goods-receipt-notes";
    public static final String SEARCH_PURCHASE_ORDER_PATH = GOODS_RECEIPT_NOTE_PATH + "/search-purchase-order";
    public static final String START_RECEIVING_PO_PATH = GOODS_RECEIPT_NOTE_PATH + "/{purchaseOrderId}/receive";
    /**
     * Dependencies.
     */
    private final GoodsReceiptNoteService goodsReceiptNoteService;
    private final PurchaseOrderService purchaseOrderService;

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
    }


    @GetMapping(value = PENDING_GOODS_RECEIPT_NOTE_PATH)
    public String pendingGoodsReceiptNotes(Model model) {
        model.addAttribute("goodsReceiptNotes", goodsReceiptNoteService.findAllPending());
        model.addAttribute("title", "Pending Goods Receipt Notes");
        return "goodsReceiptNotes/pendingGoodsReceiptNotes";
    }

    @GetMapping(value = FULFILLED_GOODS_RECEIPT_NOTE_PATH)
    public String fulfilledGoodsReceiptNotes(Model model) {
        model.addAttribute("goodsReceiptNotes", goodsReceiptNoteService.findAllFulfilled());
        model.addAttribute("title", "Fulfilled Goods Receipt Notes");
        return "goodsReceiptNotes/goodsReceiptNotes";
    }

    @GetMapping(value = SEARCH_PURCHASE_ORDER_PATH)
    public String searchPurchaseOrder(@ModelAttribute PurchaseOrderNumberDto purchaseOrderNumberDto, Model model) {
        model.addAttribute("purchaseOrderNumberDto", new PurchaseOrderNumberDto());
        model.addAttribute("title", "Search Purchase Order");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAllPendingPurchaseOrder());
        return "goodsReceiptNotes/searchPurchaseOrder";
    }

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
            return "redirect:/goods-receipt-notes/fulfilled-goods-receipt-notes?notFound";
        }
    }

    @PostMapping(value = SEARCH_PURCHASE_ORDER_PATH)
    public String displaySearchResults(@ModelAttribute PurchaseOrderNumberDto purchaseOrderNumberDto, Model model) {
        Long purchaseOrderId = purchaseOrderNumberDto.getPurchaseOrderNumber();
        Optional<PurchaseOrder> purchaseOrder = purchaseOrderService.findById(purchaseOrderId);
        if (purchaseOrder.isPresent()) {
            model.addAttribute("purchaseOrder", purchaseOrder.get());
            model.addAttribute("title", "Search Purchase Order");
            return "goodsReceiptNotes/searchPOResults";
        } else {
            return "redirect:/goods-receipt-notes/search-purchase-order?notFound";
        }
    }

    @GetMapping(value = START_RECEIVING_PO_PATH)
    public String startReceivingShipment(@PathVariable(value = "purchaseOrderId", required = true) Long id,
            Model model) {
        // Retrieves the goods receipt note with the specified ID from the database.
        Optional<PurchaseOrder> purchaseOrder = purchaseOrderService.findById(id);
        if (purchaseOrder.isPresent() && purchaseOrder.get().getStatus() != PoStatus.RECEIVED) {

            // If purchase order is found, creates a new goods receipt note
            // and populates it with the goods receipt note data. Otherwise, redirects
            // to the goods receipt note list page with an error message.
            GoodsReceiptNote goodsReceiptNote = goodsReceiptNoteService.create(purchaseOrder.get());

            GoodsReceiptNoteDto goodsReceiptNoteDto = goodsReceiptNoteService.addGoodReceiptNoteLines(goodsReceiptNote);
            // Updates the model with the goods receipt note DTO, the list of selected
            // warehouse section IDs, the warehouse sections, and the title.
            model.addAttribute("goodsReceiptNoteDto", goodsReceiptNoteDto);
            model.addAttribute("purchaseOrder", purchaseOrder.get());
            model.addAttribute("title", "Receiving Shipment");
            model.addAttribute("counter", new Counter());
            // Returns the name of the view template for the fulfill goods receipt note
            // form.
            return "goodsReceiptNotes/fulfillGoodsReceiptNoteForm";
        } else if (purchaseOrder.isPresent() && purchaseOrder.get().getStatus() == PoStatus.RECEIVED){
            return "redirect:/goods-receipt-notes/search-purchase-order?alreadyReceived";
        } else {
             return "redirect:/goods-receipt-notes/search-purchase-order?notFound";
        }
    }

    @PostMapping(value = START_RECEIVING_PO_PATH)
    public String finishReceivingShipment(@PathVariable(value = "purchaseOrderId", required = true) Long id,
            @ModelAttribute GoodsReceiptNoteDto goodsReceiptNoteDto,
            Model model) {
        // Retrieves the goods receipt note with the specified ID from the database.
        Optional<PurchaseOrder> purchaseOrder = purchaseOrderService.findById(id);
        if (purchaseOrder.isPresent() && purchaseOrder.get().getStatus() == PoStatus.IN_TRANSIT) {
            Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findByPurchaseOrder(purchaseOrder.get().getId());
            goodsReceiptNoteService.fulfill(goodsReceiptNote.get(), goodsReceiptNoteDto);
            return "redirect:/goods-receipt-notes/search-purchase-order?received";
        } else if (purchaseOrder.isPresent() && purchaseOrder.get().getStatus() == PoStatus.RECEIVED){
            return "redirect:/goods-receipt-notes/search-purchase-order?alreadyReceived";
        } else {
            return "redirect:/goods-receipt-notes/search-purchase-order?notFound";
        }
    }

}
