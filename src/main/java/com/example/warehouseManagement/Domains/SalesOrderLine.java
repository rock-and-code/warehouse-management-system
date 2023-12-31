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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="sales_order_line")
public class SalesOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "qty")
    private int qty;
    @ManyToOne
    @JoinColumn(name= "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name= "sales_order_id") //Entity's column name
    private SalesOrder salesOrder;
    @ManyToOne
    @JoinColumn(name = "item_price_id")
    private ItemPrice itemPrice;
    public double getSubtotal() {
        return qty * itemPrice.getPrice();
    }

}
