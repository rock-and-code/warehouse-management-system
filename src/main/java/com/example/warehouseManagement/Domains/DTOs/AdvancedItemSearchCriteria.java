package com.example.warehouseManagement.Domains.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters bound from the Advanced Search modal on /items. Every field is
 * optional — null/blank means "ignore this filter" so a service call with an
 * all-blank criteria returns every item (capped by Pageable).
 */
@Data
@NoArgsConstructor
public class AdvancedItemSearchCriteria {

    /** Item id as text — supports partial matches via {@link #idMode}. */
    private String id;
    private TextMode idMode = TextMode.STARTS_WITH;

    /** Item SKU as text (the column is an int; the spec casts for partial matches). */
    private String sku;
    private TextMode skuMode = TextMode.STARTS_WITH;

    /** Description match (case-insensitive). */
    private String description;
    private TextMode descriptionMode = TextMode.CONTAINS;

    /** Vendor name match against Item.vendor.name (case-insensitive). */
    private String vendor;
    private TextMode vendorMode = TextMode.CONTAINS;

    /** True when the user actually submitted the form with any filter set. */
    public boolean isActive() {
        return (id != null && !id.isBlank())
                || (sku != null && !sku.isBlank())
                || (description != null && !description.isBlank())
                || (vendor != null && !vendor.isBlank());
    }
}
