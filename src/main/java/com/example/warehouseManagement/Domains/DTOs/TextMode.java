package com.example.warehouseManagement.Domains.DTOs;

/**
 * Shared text-match operator for advanced-search filters across every entity
 * (Customer, Vendor, Item, SalesOrder, PurchaseOrder, GoodsReceiptNote, ...).
 *
 * Originally nested inside {@code AdvancedPoSearchCriteria} (PR #21); lifted
 * here so the per-entity criteria DTOs don't have to import an unrelated DTO's
 * inner type.
 */
public enum TextMode {
    EQUALS,
    STARTS_WITH,
    CONTAINS
}
