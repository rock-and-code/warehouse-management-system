package com.example.warehouseManagement.Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.warehouseManagement.Domains.DTOs.InventorySnapshotDto;
import com.example.warehouseManagement.Domains.DTOs.OpenOrdersKpiDto;
import com.example.warehouseManagement.Security.TwoFactorAuthenticationSuccessHandler;
import com.example.warehouseManagement.Services.BackorderService;
import com.example.warehouseManagement.Services.GoodsReceiptNoteService;
import com.example.warehouseManagement.Services.PickingJobService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Services.SalesOrderService;
import com.example.warehouseManagement.Services.StockService;
import com.example.warehouseManagement.Services.UserService;

@WebMvcTest(DashBoardController.class)
class DashBoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean private SalesOrderService salesOrderService;
    @MockBean private PurchaseOrderService purchaseOrderService;
    @MockBean private StockService stockService;
    @MockBean private BackorderService backorderService;
    @MockBean private GoodsReceiptNoteService goodsReceiptNoteService;
    @MockBean private PickingJobService pickingJobService;
    // Required by SecurityConfig — not exercised by these tests but the bean
    // graph won't wire without them.
    @MockBean private UserService userService;
    @MockBean private TwoFactorAuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler;

    @Test
    void getDashBoard_authenticated_returnsDashboardViewWithAllAttributes() throws Exception {
        OpenOrdersKpiDto zeroKpi = stubKpi(0L, 0.0);
        given(salesOrderService.findOpenSalesOrdersKpi()).willReturn(zeroKpi);
        given(purchaseOrderService.findOpenPurchaseOrdersKpi()).willReturn(zeroKpi);
        given(backorderService.findBackorderKpi()).willReturn(zeroKpi);
        given(goodsReceiptNoteService.countPending()).willReturn(0L);
        given(stockService.findInventorySnapshot()).willReturn(stubInventory());
        given(salesOrderService.findDailySalesLast30Days()).willReturn(List.of());
        given(pickingJobService.countPending()).willReturn(0L);
        given(stockService.countStockOnFloor()).willReturn(0L);
        given(purchaseOrderService.findTopVendorsBySpendYtd()).willReturn(List.of());
        given(purchaseOrderService.findPoStatusBuckets()).willReturn(List.of());
        given(stockService.findReorderCandidates()).willReturn(List.of());
        given(stockService.getTopFiveMovers()).willReturn(List.of());
        given(purchaseOrderService.findAgingPurchaseOrders()).willReturn(List.of());

        mvc.perform(get("/").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/dashboard"))
                .andExpect(model().attributeExists(
                        "title", "kpiSo", "kpiPo", "kpiBackorders", "pendingGrns",
                        "inventory", "dailySales", "pendingPicks", "stockOnFloor",
                        "topVendors", "poStatusBuckets", "reorderItems",
                        "topFiveMovers", "agingPos"))
                .andExpect(model().attribute("title", "Dashboard"));
    }

    private OpenOrdersKpiDto stubKpi(long count, double total) {
        return new OpenOrdersKpiDto() {
            @Override public Long getCount() { return count; }
            @Override public Double getTotalValue() { return total; }
        };
    }

    private InventorySnapshotDto stubInventory() {
        return new InventorySnapshotDto() {
            @Override public Double getTotalValue() { return 0.0; }
            @Override public Long getTotalUnits() { return 0L; }
            @Override public Long getOosCount() { return 0L; }
            @Override public Long getLowStockCount() { return 0L; }
        };
    }
}
