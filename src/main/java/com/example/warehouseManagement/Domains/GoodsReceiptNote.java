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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "goods_receipt_note")
public class GoodsReceiptNote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "purchase_order_id") //Foreign key to purchase order
    private PurchaseOrder purchaseOrder;
    @OneToMany(mappedBy = "goodsReceiptNote", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "goods_receipt_note_lines")
    @Builder.Default
    private List<GoodsReceiptNoteLine> goodsReceiptNoteLines = new ArrayList<>();
    @Column(name = "date")
    @Builder.Default
    private LocalDate date = LocalDate.now();
    @Builder.Default
    private GrnStatus status = GrnStatus.PENDING;
    public int getTotalQty() {
        int total = 0;
        for (GoodsReceiptNoteLine goodsReceiptNoteLine : goodsReceiptNoteLines) 
            total += goodsReceiptNoteLine.getQty();
        return total;
    }

    public enum GrnStatus {
        PENDING, //0
        FULFILLED //1
    }

}