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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "purchase_order_number")
    private Long purchaseOrderNumber;
    @Column(name = "date")
    @Builder.Default
    private LocalDate date = LocalDate.now();
    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "goods_receipt_notes")
    @Builder.Default
    private List<GoodsReceiptNote> goodsReceiptNotes = new ArrayList<>();
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "purchase_order_lines")
    @Builder.Default
    private List<PurchaseOrderLine> purchaseOrderLines = new ArrayList<>();
    @Column(name = "status")
    @Builder.Default
    private PoStatus status = PoStatus.IN_TRANSIT;

    public double getTotal() {
        double total = 0;
        for (int i=0; i<purchaseOrderLines.size(); i++) {
            total += purchaseOrderLines.get(i).getProduct().getCost() * purchaseOrderLines.get(i).getQty();
        }
        return total;
    }

    public enum PoStatus {
        IN_TRANSIT, //0
        RECEIVED //1
    }
}
