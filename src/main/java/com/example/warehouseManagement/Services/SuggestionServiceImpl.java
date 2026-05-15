package com.example.warehouseManagement.Services;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Repositories.CustomerRepository;
import com.example.warehouseManagement.Repositories.ItemRepository;
import com.example.warehouseManagement.Repositories.PurchaseOrderRepository;
import com.example.warehouseManagement.Repositories.VendorRepository;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

import jakarta.annotation.PostConstruct;

/**
 * Maintains four in-memory {@link SuggestionIndex} instances keyed by
 * {@link Type}. Populated once at startup; new entities created after boot
 * won't appear in suggestions until restart — see {@link #refresh()} for the
 * escape hatch a controller can call after a create / delete to keep the
 * indexes warm.
 */
@Service
public class SuggestionServiceImpl implements SuggestionService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final WarehouseSectionRepository warehouseSectionRepository;

    private final Map<Type, SuggestionIndex> indexes = new EnumMap<>(Type.class);

    public SuggestionServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                 ItemRepository itemRepository,
                                 CustomerRepository customerRepository,
                                 VendorRepository vendorRepository,
                                 WarehouseSectionRepository warehouseSectionRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
        this.vendorRepository = vendorRepository;
        this.warehouseSectionRepository = warehouseSectionRepository;
    }

    @PostConstruct
    void warmUp() {
        refresh();
    }

    /** Rebuild every index from the current DB state. */
    public synchronized void refresh() {
        Map<Type, SuggestionIndex> next = new EnumMap<>(Type.class);
        for (Type t : Type.values()) {
            next.put(t, new SuggestionIndex());
        }

        for (PurchaseOrder po : purchaseOrderRepository.findAll()) {
            next.get(Type.PURCHASE_ORDER).add(String.valueOf(po.getId()), po.getId());
        }
        for (Item item : itemRepository.findAll()) {
            // Index by description AND by SKU — both are useful prefixes.
            next.get(Type.ITEM).add(item.getDescription(), item.getId());
            next.get(Type.ITEM).add(String.valueOf(item.getSku()), item.getId());
        }
        for (Customer c : customerRepository.findAll()) {
            next.get(Type.CUSTOMER).add(c.getName(), c.getId());
        }
        for (Vendor v : vendorRepository.findAll()) {
            next.get(Type.VENDOR).add(v.getName(), v.getId());
        }
        for (WarehouseSection ws : warehouseSectionRepository.findAll()) {
            // sectionNumber (e.g. "01-02-3-4") is the label the user types; id
            // is included so any future href-template autocomplete can navigate.
            next.get(Type.WAREHOUSE_SECTION).add(ws.getSectionNumber(), ws.getId());
        }

        indexes.clear();
        indexes.putAll(next);
    }

    @Override
    public List<Suggestion> suggest(Type type, Mode mode, String query, int limit) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        SuggestionIndex index = indexes.get(type);
        if (index == null) {
            return List.of();
        }
        List<SuggestionIndex.Match> matches = (mode == Mode.CONTAINS)
                ? index.findContaining(query, limit)
                : index.findByPrefix(query, limit);
        return matches.stream()
                .map(m -> new Suggestion(m.label(), m.id(), type))
                .toList();
    }
}
