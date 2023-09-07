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
import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;
import com.example.warehouseManagement.Services.GoodsReceiptNoteService;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Util.Counter;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/")
public class GoodsReceiptNoteController {
    public static final String GOODS_RECEIPT_NOTE_PATH = "goods-receipt-note";
    public static final String NEW_GOODS_RECEIPT_NOTE_PATH = GOODS_RECEIPT_NOTE_PATH+ "/new-good-receipt-note";
    public static final String GOODS_RECEIPT_NOTE_ID_PATH = GOODS_RECEIPT_NOTE_PATH + "/{goodsReceiptNoteId}";
    private final GoodsReceiptNoteService goodsReceiptNoteService;
    private final PurchaseOrderService purchaseOrderService;
    private final ItemService itemService;
    public GoodsReceiptNoteController(GoodsReceiptNoteService goodsReceiptNoteService,
            PurchaseOrderService purchaseOrderService, ItemService itemService) {
        this.goodsReceiptNoteService = goodsReceiptNoteService;
        this.purchaseOrderService = purchaseOrderService;
        this.itemService = itemService;
    }

    @GetMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH)
    public String newGoodsReceiptNote(@ModelAttribute GoodsReceiptNote goodsReceiptNote, Model model) {
        model.addAttribute("goodsReceiptNote", new GoodsReceiptNote());
        model.addAttribute("title", "New Goods Receipt Note");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/goodsReceiptNoteForm";
    }

    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "addRow")
    public String addGoodReceiptNoteLine(@ModelAttribute GoodsReceiptNote goodsReceiptNote,
            HttpServletRequest request, Model model) {
        //Adding new goods receipt note line to current goods receipt note
        goodsReceiptNote.getGoodsReceiptNoteLines().add(new GoodsReceiptNoteLine());
        model.addAttribute("title", "New Goods Receipt Note");    
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/goodsReceiptNoteForm";
    }

    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "removeRow")
    public String removeGoodsReceiptNoteLine(@ModelAttribute GoodsReceiptNote goodsReceiptNote,
            HttpServletRequest request, Model model) {
        // newSalesOrderDto.addOrderLine();
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        goodsReceiptNote.getGoodsReceiptNoteLines().remove(rowId);
        model.addAttribute("title", "New Goods Receipt Note");
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/goodsReceiptNoteForm";
    }

    @PostMapping(value = NEW_GOODS_RECEIPT_NOTE_PATH, params = "save")
    public String saveNewGoodsReceiptNote(@ModelAttribute GoodsReceiptNote goodsReceiptNote) {
        goodsReceiptNoteService.save(goodsReceiptNote);
        return "redirect:/goods-receipt-note?added";
    }

    @GetMapping(value = GOODS_RECEIPT_NOTE_PATH)
    public String getAllGoodsReceiptNotes(Model model) {
        model.addAttribute("title", "Goods Receipt Notes");
        model.addAttribute("goodsReceiptNotes",goodsReceiptNoteService.findAll());
        return "goodsReceiptNotes/goodsReceiptNotes";
    }

    @GetMapping(value = GOODS_RECEIPT_NOTE_ID_PATH)
    public String getGoodsReceiptNoteDetails(@PathVariable(value = "goodsReceiptNoteId", required = false) Long id, Model model) {
        Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findById(id);
        if (goodsReceiptNote.isPresent()) {
            model.addAttribute("title", "Goods Receipt Note Details");
            model.addAttribute("goodsReceiptNote", goodsReceiptNote.get());
            model.addAttribute("counter", new Counter());
            return "goodsReceiptNotes/goodsReceiptNoteDetails";
        }
        else {
            return "redirect:/goods-receipt-note?notFound";
        }
    }

    // @PostMapping(value = GOODS_RECEIPT_NOTE_ID_PATH)
    // public String deleteGoodsReceiptNotes(@PathVariable(name = "goodsReceiptNoteId", required = false) Long id, Model model) {
    //     Optional<GoodsReceiptNote> goodsReceiptNote = goodsReceiptNoteService.findById(id);
    //     if (goodsReceiptNote.isPresent()) {
    //         goodsReceiptNoteService.delete(goodsReceiptNote.get());
    //         return "redirect:/goodsReceiptNotes?deleted";
    //     }
    //     else {
    //         return "redirect:/goodsReceiptNotes?notFound";
    //     }
    // }
    
}
