package com.example.warehouseManagement.Services;

import java.util.List;

public interface SuggestionService {

    /** Suggestion categories the autocomplete + advanced search can query. */
    enum Type {
        PURCHASE_ORDER,
        ITEM,
        CUSTOMER,
        VENDOR
    }

    enum Mode {
        PREFIX,
        CONTAINS
    }

    /**
     * Single-category lookup. Returns at most {@code limit} matches as
     * {label, id} pairs the caller can render into a dropdown.
     */
    List<Suggestion> suggest(Type type, Mode mode, String query, int limit);

    /** Lightweight DTO for the JSON endpoint. */
    record Suggestion(String label, Long id, Type type) {}
}
