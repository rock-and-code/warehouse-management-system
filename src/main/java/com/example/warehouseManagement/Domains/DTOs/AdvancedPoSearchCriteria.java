package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.warehouseManagement.Domains.PurchaseOrder.PoStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters bound from the Advanced Search modal on /goods-receipt-notes/search-purchase-order.
 * Every field is optional — null/blank means "ignore this filter" so a service call with
 * an all-blank criteria returns everything (capped by Pageable).
 */
@Data
@NoArgsConstructor
public class AdvancedPoSearchCriteria {

    /** PO id as text — supports partial matches via {@link #idMode}. */
    private String id;
    private TextMode idMode = TextMode.STARTS_WITH;

    /** Vendor name match against PurchaseOrder.vendor.name. */
    private String vendor;
    private TextMode vendorMode = TextMode.CONTAINS;

    /** Inclusive date range on PurchaseOrder.date. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    /** Optional status. Null means "any status". */
    private PoStatus status;

    /** True when the user actually submitted the advanced form (any field present). */
    public boolean isActive() {
        return (id != null && !id.isBlank())
                || (vendor != null && !vendor.isBlank())
                || dateFrom != null
                || dateTo != null
                || status != null;
    }
}
