package com.example.warehouseManagement.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.ItemCost;
import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrder.PoStatus;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.DTOs.AgingPoDto;
import com.example.warehouseManagement.Domains.DTOs.OpenOrdersKpiDto;
import com.example.warehouseManagement.Domains.DTOs.PoStatusBucketDto;
import com.example.warehouseManagement.Domains.DTOs.VendorSpendDto;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PurchaseOrderRepositoryTest {

    @Autowired
    private PurchaseOrderRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findOpenPurchaseOrdersKpi_includesInTransitAndPartiallyReceived() {
        Vendor vendor = persistVendor("Acme");
        Item widget = persistItem(vendor, 100, "Widget");
        ItemCost cost = persistCost(widget, 5.0);

        // IN_TRANSIT — counted
        persistPoWithLine(vendor, PoStatus.IN_TRANSIT, LocalDate.now(), widget, cost, 4);
        // PARTIALLY_RECEIVED — counted
        persistPoWithLine(vendor, PoStatus.PARTIALLY_RECEIVED, LocalDate.now(), widget, cost, 6);
        // RECEIVED — excluded
        persistPoWithLine(vendor, PoStatus.RECEIVED, LocalDate.now(), widget, cost, 99);

        em.flush();
        em.clear();

        OpenOrdersKpiDto kpi = repository.findOpenPurchaseOrdersKpi();

        assertThat(kpi.getCount()).isEqualTo(2L);
        assertThat(kpi.getTotalValue()).isEqualTo(50.0); // (4+6) * 5
    }

    @Test
    void findTopVendorsBySpendYtd_aggregatesAndSortsDescending() {
        Vendor acme = persistVendor("Acme");
        Vendor wayne = persistVendor("Wayne");
        Item a = persistItem(acme, 100, "A");
        Item w = persistItem(wayne, 200, "W");
        ItemCost aCost = persistCost(a, 10.0);
        ItemCost wCost = persistCost(w, 20.0);

        // Acme: 2 units @ $10 = $20
        persistPoWithLine(acme, PoStatus.IN_TRANSIT, LocalDate.now(), a, aCost, 2);
        // Wayne: 5 units @ $20 = $100
        persistPoWithLine(wayne, PoStatus.RECEIVED, LocalDate.now(), w, wCost, 5);
        // Last year — excluded
        persistPoWithLine(acme, PoStatus.RECEIVED, LocalDate.now().minusYears(1).withDayOfYear(1), a, aCost, 99);

        em.flush();
        em.clear();

        List<VendorSpendDto> top = repository.findTopVendorsBySpendYtd();

        assertThat(top).hasSize(2);
        assertThat(top.get(0).getVendorName()).isEqualTo("Wayne");
        assertThat(top.get(0).getTotal()).isEqualTo(100.0);
        assertThat(top.get(1).getVendorName()).isEqualTo("Acme");
        assertThat(top.get(1).getTotal()).isEqualTo(20.0);
    }

    @Test
    void findPoStatusBuckets_groupsByStatus() {
        Vendor vendor = persistVendor("Acme");
        Item widget = persistItem(vendor, 100, "Widget");
        ItemCost cost = persistCost(widget, 5.0);

        persistPoWithLine(vendor, PoStatus.IN_TRANSIT, LocalDate.now(), widget, cost, 1);
        persistPoWithLine(vendor, PoStatus.IN_TRANSIT, LocalDate.now(), widget, cost, 1);
        persistPoWithLine(vendor, PoStatus.RECEIVED, LocalDate.now(), widget, cost, 1);

        em.flush();
        em.clear();

        List<PoStatusBucketDto> buckets = repository.findPoStatusBuckets();

        // Two rows: status 0 (IN_TRANSIT) = 2, status 1 (RECEIVED) = 1, ordered by status
        assertThat(buckets).hasSize(2);
        assertThat(buckets.get(0).getStatus()).isEqualTo(0);
        assertThat(buckets.get(0).getCount()).isEqualTo(2L);
        assertThat(buckets.get(1).getStatus()).isEqualTo(1);
        assertThat(buckets.get(1).getCount()).isEqualTo(1L);
    }

    @Test
    void findAgingPurchaseOrders_onlyOpenAndOlderThan30Days() {
        Vendor vendor = persistVendor("Acme");
        Item widget = persistItem(vendor, 100, "Widget");
        ItemCost cost = persistCost(widget, 5.0);

        // Open + 45 days old — included
        persistPoWithLine(vendor, PoStatus.IN_TRANSIT, LocalDate.now().minusDays(45), widget, cost, 2);
        // Open + 31 days old — included
        persistPoWithLine(vendor, PoStatus.PARTIALLY_RECEIVED, LocalDate.now().minusDays(31), widget, cost, 3);
        // Open + 10 days old — excluded (too recent)
        persistPoWithLine(vendor, PoStatus.IN_TRANSIT, LocalDate.now().minusDays(10), widget, cost, 4);
        // Received + 90 days old — excluded (already closed)
        persistPoWithLine(vendor, PoStatus.RECEIVED, LocalDate.now().minusDays(90), widget, cost, 5);

        em.flush();
        em.clear();

        List<AgingPoDto> aging = repository.findAgingPurchaseOrders();

        assertThat(aging).hasSize(2);
        // ORDER BY ageDays DESC — 45-day row first
        assertThat(aging.get(0).getAgeDays()).isEqualTo(45);
        assertThat(aging.get(1).getAgeDays()).isEqualTo(31);
    }

    // ---------- fixture helpers ----------

    private Vendor persistVendor(String name) {
        return em.persist(Vendor.builder().name(name).build());
    }

    private Item persistItem(Vendor vendor, int sku, String description) {
        return em.persist(Item.builder().vendor(vendor).sku(sku).description(description).build());
    }

    private ItemCost persistCost(Item item, double cost) {
        return em.persist(ItemCost.builder()
                .item(item)
                .start(LocalDate.now().minusYears(1))
                .end(LocalDate.now().plusYears(1))
                .cost(cost)
                .build());
    }

    private void persistPoWithLine(Vendor vendor, PoStatus status, LocalDate date,
                                   Item item, ItemCost cost, int qty) {
        PurchaseOrder po = em.persist(PurchaseOrder.builder()
                .vendor(vendor).status(status).date(date).build());
        em.persist(PurchaseOrderLine.builder()
                .purchaseOrder(po).item(item).itemCost(cost).qty(qty).build());
    }
}
