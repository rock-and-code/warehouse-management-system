package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.warehouseManagement.Domains.PickingJob.PjStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters bound from the Advanced Search modal on /picking-jobs. Every field
 * is optional — null/blank means "ignore this filter" so a service call with
 * an all-blank criteria returns every picking job (capped by Pageable).
 */
@Data
@NoArgsConstructor
public class AdvancedPickingJobSearchCriteria {

    /** Picking job id as text — supports partial matches via {@link #idMode}. */
    private String id;
    private TextMode idMode = TextMode.STARTS_WITH;

    /** Sales-order id — joined through {@code salesOrder.id}. */
    private String salesOrderId;
    private TextMode salesOrderIdMode = TextMode.STARTS_WITH;

    /** Customer name match against PickingJob.salesOrder.customer.name. */
    private String customer;
    private TextMode customerMode = TextMode.CONTAINS;

    /** Inclusive date range on PickingJob.date. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    /** Optional status. Null means "any status". */
    private PjStatus status;

    /** True when the user actually submitted the form with any filter set. */
    public boolean isActive() {
        return (id != null && !id.isBlank())
                || (salesOrderId != null && !salesOrderId.isBlank())
                || (customer != null && !customer.isBlank())
                || dateFrom != null
                || dateTo != null
                || status != null;
    }
}
