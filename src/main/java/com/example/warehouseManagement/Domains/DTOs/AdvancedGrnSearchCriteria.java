package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.warehouseManagement.Domains.GoodsReceiptNote.GrnStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters bound from the Advanced Search modal on /goods-receipt-notes. Every
 * field is optional — null/blank means "ignore this filter" so a service call
 * with an all-blank criteria returns every GRN (capped by Pageable).
 */
@Data
@NoArgsConstructor
public class AdvancedGrnSearchCriteria {

    /** GRN id as text — supports partial matches via {@link #idMode}. */
    private String id;
    private TextMode idMode = TextMode.STARTS_WITH;

    /** Purchase-order id match — joined through {@code purchaseOrder.id}. */
    private String purchaseOrderId;
    private TextMode purchaseOrderIdMode = TextMode.STARTS_WITH;

    /** Vendor name match against GoodsReceiptNote.purchaseOrder.vendor.name. */
    private String vendor;
    private TextMode vendorMode = TextMode.CONTAINS;

    /** Inclusive date range on GoodsReceiptNote.date. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    /** Optional status. Null means "any status". */
    private GrnStatus status;

    /** True when the user actually submitted the form with any filter set. */
    public boolean isActive() {
        return (id != null && !id.isBlank())
                || (purchaseOrderId != null && !purchaseOrderId.isBlank())
                || (vendor != null && !vendor.isBlank())
                || dateFrom != null
                || dateTo != null
                || status != null;
    }
}
