package com.example.warehouseManagement.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.ItemCost;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.Warehouse;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.InventorySnapshotDto;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class StockRepositoryTest {

    @Autowired
    private StockRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findInventorySnapshot_sumsValueAndUnits() {
        Vendor vendor = em.persist(Vendor.builder().name("Acme").build());
        Item widget = em.persist(Item.builder().vendor(vendor).sku(100).description("Widget").build());
        Item gadget = em.persist(Item.builder().vendor(vendor).sku(200).description("Gadget").build());
        // Active cost @ $5 for widget, $2 for gadget
        em.persist(ItemCost.builder().item(widget).cost(5.0)
                .start(LocalDate.now().minusYears(1)).end(LocalDate.now().plusYears(1)).build());
        em.persist(ItemCost.builder().item(gadget).cost(2.0)
                .start(LocalDate.now().minusYears(1)).end(LocalDate.now().plusYears(1)).build());

        Warehouse w = em.persist(Warehouse.builder().address("123 Main").build());
        WarehouseSection sec = em.persist(WarehouseSection.builder().warehouse(w).sectionNumber("A1").build());

        // 4 widgets @ $5  +  10 gadgets @ $2  = $40
        em.persist(Stock.builder().warehouseSection(sec).item(widget).qtyOnHand(4).build());
        em.persist(Stock.builder().warehouseSection(sec).item(gadget).qtyOnHand(10).build());

        em.flush();
        em.clear();

        InventorySnapshotDto snap = repository.findInventorySnapshot();

        assertThat(snap.getTotalUnits()).isEqualTo(14L);
        assertThat(snap.getTotalValue()).isEqualTo(40.0);
        // Both items have stock > 0, so neither is out-of-stock.
        assertThat(snap.getOosCount()).isEqualTo(0L);
    }

    @Test
    void findInventorySnapshot_countsItemsWithNoStockAsOos() {
        Vendor vendor = em.persist(Vendor.builder().name("Acme").build());
        // Two items, neither has a Stock row → both count as OOS
        em.persist(Item.builder().vendor(vendor).sku(100).description("Widget").build());
        em.persist(Item.builder().vendor(vendor).sku(200).description("Gadget").build());

        em.flush();
        em.clear();

        InventorySnapshotDto snap = repository.findInventorySnapshot();

        assertThat(snap.getTotalUnits()).isEqualTo(0L);
        assertThat(snap.getTotalValue()).isEqualTo(0.0);
        assertThat(snap.getOosCount()).isEqualTo(2L);
    }

    @Test
    void findInventorySnapshot_emptyDb_returnsAllZeros() {
        InventorySnapshotDto snap = repository.findInventorySnapshot();

        assertThat(snap.getTotalUnits()).isEqualTo(0L);
        assertThat(snap.getTotalValue()).isEqualTo(0.0);
        assertThat(snap.getOosCount()).isEqualTo(0L);
        assertThat(snap.getLowStockCount()).isEqualTo(0L);
    }
}
