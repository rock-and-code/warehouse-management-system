package com.example.warehouseManagement.Domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "goods_receipt_note_line")
public class GoodsReceiptNoteLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "goods_receipt_note_id") //Foreign key to goods receipt note entity
    private GoodsReceiptNote goodsReceiptNote;
    @Column(name = "damaged")
    private boolean damaged;
    @Column(name = "qty")
    private int qty;
    @ManyToOne
    @JoinColumn(name= "item_id") //Map foreign key in product entity
    private Item item;
}
