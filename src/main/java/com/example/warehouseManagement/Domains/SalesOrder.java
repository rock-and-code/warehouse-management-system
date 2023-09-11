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
@Table(name="sales_order")
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "date")
    @Builder.Default
    private LocalDate date =  LocalDate.now();
    @ManyToOne
    @JoinColumn(name = "customer_id") //Foreing key to customer
    private Customer customer;
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY) //mappedBy foreign key to sales order line entity. It uses the Sales order entity property name
    @Column(name = "picking_jobs")
    @Builder.Default
    private List<PickingJob> pickingJobs = new ArrayList<>();
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY) //mappedBy foreign key to sales order line entity. It uses the Sales order entity property name
    @Column(name = "sales_order_lines")
    @Builder.Default
    private List<SalesOrderLine> saleOrderLines = new ArrayList<>();
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY) //mappedBy foreign key to sales order line entity. It uses the Sales order entity property name
    @Column(name = "invoices")
    @Builder.Default
    private List<Invoice> invoices = new ArrayList<>();
    @Column(name = "status")
    @Builder.Default
    private SoStatus status = SoStatus.PENDING;

    //TC(M*N)
    public double getTotal() {
        double total = 0;
        for (int i=0; i<saleOrderLines.size(); i++) {
            total += saleOrderLines.get(i).getItemPrice().getPrice() * saleOrderLines.get(i).getQty();
        }
        return total;
    }
    
    public enum SoStatus {
        PENDING, //0
        SHIPPED //1
    }
}

/**
 * @JoinTable(name = "SALEORDER_ORDERLINE", joinColumns = @JoinColumn(name="SALEORDER_ID"),
        inverseJoinColumns = @JoinColumn(name="SALEORDERLINE_ID"))
 */