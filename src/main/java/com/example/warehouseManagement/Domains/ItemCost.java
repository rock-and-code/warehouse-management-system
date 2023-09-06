package com.example.warehouseManagement.Domains;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "item_cost")
public class ItemCost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @OneToMany(mappedBy = "itemCost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "purchase_order_line_id")
    @Builder.Default
    private List<PurchaseOrderLine> purchaseOrderLine = new ArrayList<>();
    @Column(name = "start_date")
    private LocalDate start;
    @Column(name = "end_date")
    private LocalDate end;
    @Column(name = "cost")
    private double cost;
}

/**
 * Annotations for many to many for furture if needed
 * @JoinTable(name = "purchase_order_line_cost", joinColumns = @JoinColumn(name="cost_id"),
        inverseJoinColumns = @JoinColumn(name="purchase_order_line_id"))
 */