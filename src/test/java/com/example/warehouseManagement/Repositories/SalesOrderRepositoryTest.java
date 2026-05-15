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

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.ItemPrice;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.SalesOrder.SoStatus;
import com.example.warehouseManagement.Domains.SalesOrderLine;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.DTOs.DailySalesDto;
import com.example.warehouseManagement.Domains.DTOs.OpenOrdersKpiDto;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class SalesOrderRepositoryTest {

    @Autowired
    private SalesOrderRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findOpenSalesOrdersKpi_countsPendingAndPartiallyShipped() {
        Vendor vendor = persistVendor("Acme");
        Item widget = persistItem(vendor, 100, "Widget");
        ItemPrice price = persistPrice(widget, 10.0);
        Customer customer = persistCustomer("Bob");

        // PENDING (status=0) — qty 3 @ $10 → $30
        SalesOrder open1 = persistSalesOrder(customer, SoStatus.PENDING, LocalDate.now());
        persistLine(open1, widget, price, 3);
        // PARTIALLY_SHIPPED (status=1) — qty 2 @ $10 → $20
        SalesOrder open2 = persistSalesOrder(customer, SoStatus.PARTIALLY_SHIPPED, LocalDate.now());
        persistLine(open2, widget, price, 2);
        // SHIPPED (status=2) — excluded
        SalesOrder closed = persistSalesOrder(customer, SoStatus.SHIPPED, LocalDate.now());
        persistLine(closed, widget, price, 99);

        em.flush();
        em.clear();

        OpenOrdersKpiDto kpi = repository.findOpenSalesOrdersKpi();

        assertThat(kpi.getCount()).isEqualTo(2L);
        assertThat(kpi.getTotalValue()).isEqualTo(50.0);
    }

    @Test
    void findOpenSalesOrdersKpi_emptyDb_returnsZeroes() {
        OpenOrdersKpiDto kpi = repository.findOpenSalesOrdersKpi();

        assertThat(kpi.getCount()).isEqualTo(0L);
        assertThat(kpi.getTotalValue()).isEqualTo(0.0);
    }

    @Test
    void findDailySalesLast30Days_groupsByDayAndExcludesOlder() {
        Vendor vendor = persistVendor("Acme");
        Item widget = persistItem(vendor, 100, "Widget");
        ItemPrice price = persistPrice(widget, 10.0);
        Customer customer = persistCustomer("Bob");

        LocalDate today = LocalDate.now();
        // Today: 2 orders, 5 + 3 units = 80
        addOrderWithLineOnDate(customer, widget, price, today, 5);
        addOrderWithLineOnDate(customer, widget, price, today, 3);
        // 10 days ago: 1 order, 4 units = 40
        addOrderWithLineOnDate(customer, widget, price, today.minusDays(10), 4);
        // 40 days ago: excluded
        addOrderWithLineOnDate(customer, widget, price, today.minusDays(40), 99);

        em.flush();
        em.clear();

        List<DailySalesDto> daily = repository.findDailySalesLast30Days();

        assertThat(daily).hasSize(2);
        assertThat(daily).extracting(DailySalesDto::getTotal)
                .containsExactly(40.0, 80.0); // ORDER BY so.date ASC
    }

    // ---------- fixture helpers ----------

    private Vendor persistVendor(String name) {
        return em.persist(Vendor.builder().name(name).build());
    }

    private Item persistItem(Vendor vendor, int sku, String description) {
        return em.persist(Item.builder().vendor(vendor).sku(sku).description(description).build());
    }

    private ItemPrice persistPrice(Item item, double price) {
        return em.persist(ItemPrice.builder()
                .item(item)
                .start(LocalDate.now().minusYears(1))
                .end(LocalDate.now().plusYears(1))
                .price(price)
                .build());
    }

    private Customer persistCustomer(String name) {
        return em.persist(Customer.builder().name(name).build());
    }

    private SalesOrder persistSalesOrder(Customer customer, SoStatus status, LocalDate date) {
        return em.persist(SalesOrder.builder().customer(customer).status(status).date(date).build());
    }

    private SalesOrderLine persistLine(SalesOrder so, Item item, ItemPrice price, int qty) {
        return em.persist(SalesOrderLine.builder()
                .salesOrder(so).item(item).itemPrice(price).qty(qty).build());
    }

    private void addOrderWithLineOnDate(Customer customer, Item item, ItemPrice price,
                                        LocalDate date, int qty) {
        SalesOrder so = persistSalesOrder(customer, SoStatus.PENDING, date);
        persistLine(so, item, price, qty);
    }
}
