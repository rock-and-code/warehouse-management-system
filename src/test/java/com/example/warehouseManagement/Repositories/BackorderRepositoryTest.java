package com.example.warehouseManagement.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.ItemPrice;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.DTOs.OpenOrdersKpiDto;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BackorderRepositoryTest {

    @Autowired
    private BackorderRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findBackorderKpi_aggregatesCountAndCurrentPriceValue() {
        Vendor vendor = em.persist(Vendor.builder().name("Acme").build());
        Item widget = em.persist(Item.builder().vendor(vendor).sku(100).description("Widget").build());
        // Active price: today is within [start, end]
        em.persist(ItemPrice.builder()
                .item(widget)
                .start(LocalDate.now().minusDays(30))
                .end(LocalDate.now().plusDays(30))
                .price(7.0).build());
        Customer customer = em.persist(Customer.builder().name("Bob").build());
        SalesOrder so = em.persist(SalesOrder.builder().customer(customer).date(LocalDate.now()).build());

        em.persist(Backorder.builder().salesOrder(so).item(widget).qty(2).build()); // 2 * 7 = 14
        em.persist(Backorder.builder().salesOrder(so).item(widget).qty(3).build()); // 3 * 7 = 21

        em.flush();
        em.clear();

        OpenOrdersKpiDto kpi = repository.findBackorderKpi();

        assertThat(kpi.getCount()).isEqualTo(2L);
        assertThat(kpi.getTotalValue()).isEqualTo(35.0);
    }

    @Test
    void findBackorderKpi_emptyDb_returnsZeroes() {
        OpenOrdersKpiDto kpi = repository.findBackorderKpi();

        assertThat(kpi.getCount()).isEqualTo(0L);
        assertThat(kpi.getTotalValue()).isEqualTo(0.0);
    }
}
