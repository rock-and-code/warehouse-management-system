package com.example.warehouseManagement.Domains;

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
@Table(name="item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
    @Column(name = "sku")
    private int sku;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "item_prices")
    @Builder.Default
    private List<ItemPrice> itemPrices = new ArrayList<>();
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "item_costs")
    @Builder.Default
    private List<ItemCost> itemCosts = new ArrayList<>();
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "goods_receipt_note_lines")
    @Builder.Default
    private List<GoodsReceiptNoteLine> goodsReceiptNoteLines = new ArrayList<>();
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "sales_order_lines")
    @Builder.Default
    private List<SalesOrderLine> salesOrderLines = new ArrayList<>();
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "purchase_order_lines")
    @Builder.Default
    private List<PurchaseOrderLine> purchaseOrderLines = new ArrayList<>();
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "picking_job_lines")
    @Builder.Default
    private List<PickingJobLine> pickingJobLines = new ArrayList<>();
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "invoice_lines")
    @Builder.Default
    private List<InvoiceLine> invoiceLines = new ArrayList<>();
    
}
