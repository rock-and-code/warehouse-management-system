package com.example.warehouseManagement.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Services.BackorderService;
import com.example.warehouseManagement.Services.GoodsReceiptNoteService;
import com.example.warehouseManagement.Services.PickingJobService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Services.SalesOrderService;
import com.example.warehouseManagement.Services.StockService;

@Controller
@RequestMapping(value = "/")
public class DashBoardController {

    private final SalesOrderService salesOrderService;
    private final PurchaseOrderService purchaseOrderService;
    private final StockService stockService;
    private final BackorderService backorderService;
    private final GoodsReceiptNoteService goodsReceiptNoteService;
    private final PickingJobService pickingJobService;

    public DashBoardController(SalesOrderService salesOrderService,
                               PurchaseOrderService purchaseOrderService,
                               StockService stockService,
                               BackorderService backorderService,
                               GoodsReceiptNoteService goodsReceiptNoteService,
                               PickingJobService pickingJobService) {
        this.salesOrderService = salesOrderService;
        this.purchaseOrderService = purchaseOrderService;
        this.stockService = stockService;
        this.backorderService = backorderService;
        this.goodsReceiptNoteService = goodsReceiptNoteService;
        this.pickingJobService = pickingJobService;
    }

    @GetMapping(value = "/")
    public String getDashBoard(Model model) {
        model.addAttribute("title", "Dashboard");
        // Row 1 — KPI cards
        model.addAttribute("kpiSo",          salesOrderService.findOpenSalesOrdersKpi());
        model.addAttribute("kpiPo",          purchaseOrderService.findOpenPurchaseOrdersKpi());
        model.addAttribute("kpiBackorders",  backorderService.findBackorderKpi());
        model.addAttribute("pendingGrns",    goodsReceiptNoteService.countPending());
        // Row 2 — inventory snapshot tiles
        model.addAttribute("inventory",      stockService.findInventorySnapshot());
        // Row 3 — daily sales trend + ops stats
        model.addAttribute("dailySales",     salesOrderService.findDailySalesLast30Days());
        model.addAttribute("pendingPicks",   pickingJobService.countPending());
        model.addAttribute("stockOnFloor",   stockService.countStockOnFloor());
        // Row 4 — vendor spend + PO status doughnut
        model.addAttribute("topVendors",     purchaseOrderService.findTopVendorsBySpendYtd());
        model.addAttribute("poStatusBuckets",purchaseOrderService.findPoStatusBuckets());
        // Row 5 — reorder + top movers
        model.addAttribute("reorderItems",   stockService.findReorderCandidates());
        model.addAttribute("topFiveMovers",  stockService.getTopFiveMovers());
        // Row 6 — PO aging
        model.addAttribute("agingPos",       purchaseOrderService.findAgingPurchaseOrders());
        return "dashboard/dashboard";
    }
}
