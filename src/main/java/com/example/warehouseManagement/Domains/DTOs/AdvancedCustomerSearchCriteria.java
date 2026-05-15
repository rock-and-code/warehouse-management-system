package com.example.warehouseManagement.Domains.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters bound from the Advanced Search modal on /customers. Every field is
 * optional — null/blank means "ignore this filter" so a service call with an
 * all-blank criteria returns every customer (capped by Pageable).
 */
@Data
@NoArgsConstructor
public class AdvancedCustomerSearchCriteria {

    /** Customer id as text — supports partial matches via {@link #idMode}. */
    private String id;
    private TextMode idMode = TextMode.STARTS_WITH;

    /** Name match (case-insensitive). */
    private String name;
    private TextMode nameMode = TextMode.CONTAINS;

    /** City match (case-insensitive). */
    private String city;
    private TextMode cityMode = TextMode.CONTAINS;

    /** State match (case-insensitive). */
    private String state;
    private TextMode stateMode = TextMode.CONTAINS;

    /** True when the user actually submitted the form with any filter set. */
    public boolean isActive() {
        return (id != null && !id.isBlank())
                || (name != null && !name.isBlank())
                || (city != null && !city.isBlank())
                || (state != null && !state.isBlank());
    }
}
