package com.example.warehouseManagement.Domains;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "stock_adjustment")
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AdjustmentType type;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "source_warehouse_section_id", nullable = false)
    private WarehouseSection source;

    @ManyToOne
    @JoinColumn(name = "destination_warehouse_section_id")
    private WarehouseSection destination;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "adjustment_date", nullable = false)
    @Builder.Default
    private LocalDate adjustmentDate = LocalDate.now();

    @Column(name = "notes")
    private String notes;

    public enum AdjustmentType {
        CYCLE_COUNT,
        DAMAGE,
        WRITE_OFF,
        TRANSFER
    }
}
