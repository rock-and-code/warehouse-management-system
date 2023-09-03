package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// @Data
// @AllArgsConstructor
// @NoArgsConstructor
public class NewSalesOrderDto {
    private Long salesOrderNumber;
    private LocalDate date;
    private Long customerId;
    private List<SalesOrderLineDto> saleOrderLines = new ArrayList<>();
    
   



    public NewSalesOrderDto(Long salesOrderNumber, LocalDate date, Long customerId,
            List<SalesOrderLineDto> saleOrderLines) {
        this.salesOrderNumber = salesOrderNumber;
        this.date = date;
        this.customerId = customerId;
        this.saleOrderLines = saleOrderLines;
    }





    public NewSalesOrderDto() {
        this.date = LocalDate.now();
    }





    public Long getSalesOrderNumber() {
        return salesOrderNumber;
    }





    public void setSalesOrderNumber(Long salesOrderNumber) {
        this.salesOrderNumber = salesOrderNumber;
    }





    public LocalDate getDate() {
        return date;
    }





    public void setDate(LocalDate date) {
        this.date = date;
    }





    public Long getCustomerId() {
        return customerId;
    }





    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }





    public List<SalesOrderLineDto> getSaleOrderLines() {
        return saleOrderLines;
    }





    public void setSaleOrderLines(List<SalesOrderLineDto> saleOrderLines) {
        this.saleOrderLines = saleOrderLines;
    }

    



    // public void addOrderLine() {
    //     this.saleOrderLines.add(new SalesOrderLineDto());
    // }

    // public void deleteLastOrderLine() {
    //     this.saleOrderLines.remove(this.getSaleOrderLines().size()-1);
    // }

    // public void deleteOrderLine(int index) {
    //     if (index > 0 || index < this.getSaleOrderLines().size()) {
    //         this.saleOrderLines.remove(index);
    //     }
    // }


}
