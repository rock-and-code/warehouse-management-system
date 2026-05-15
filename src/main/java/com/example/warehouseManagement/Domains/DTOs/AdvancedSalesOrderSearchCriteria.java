package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.warehouseManagement.Domains.SalesOrder.SoStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters bound from the Advanced Search modal on /sales-orders. Every field
 * is optional — null/blank means "ignore this filter" so a service call with
 * an all-blank criteria returns every sales order (capped by Pageable).
 */
@Data
@NoArgsConstructor
public class AdvancedSalesOrderSearchCriteria {

    /** SO id as text — supports partial matches via {@link #idMode}. */
    private String id;
    private TextMode idMode = TextMode.STARTS_WITH;

    /** Customer name match against SalesOrder.customer.name. */
    private String customer;
    private TextMode customerMode = TextMode.CONTAINS;

    /** Inclusive date range on SalesOrder.date. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    /** Optional status. Null means "any status". */
    private SoStatus status;

    /** True when the user actually submitted the form with any filter set. */
    public boolean isActive() {
        return (id != null && !id.isBlank())
                || (customer != null && !customer.isBlank())
                || dateFrom != null
                || dateTo != null
                || status != null;
    }
}
