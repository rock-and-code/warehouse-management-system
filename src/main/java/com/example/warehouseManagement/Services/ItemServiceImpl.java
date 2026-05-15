package com.example.warehouseManagement.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.DTOs.AdvancedItemSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.TextMode;
import com.example.warehouseManagement.Domains.Exceptions.ItemNotFoundException;
import com.example.warehouseManagement.Repositories.ItemRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    /**
     * Returns an interable of the list of all the items in the dba
     * @return
     */
    @Override
    public Iterable<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Page<Item> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @Override
    public Page<Item> findAdvanced(AdvancedItemSearchCriteria criteria, Pageable pageable) {
        return itemRepository.findAll(buildSpec(criteria), pageable);
    }

    private static Specification<Item> buildSpec(AdvancedItemSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            // id (Long) — EQUALS uses numeric equality; STARTS_WITH/CONTAINS cast to text.
            if (c.getId() != null && !c.getId().isBlank()) {
                String idStr = c.getId().trim();
                TextMode mode = c.getIdMode() == null ? TextMode.STARTS_WITH : c.getIdMode();
                if (mode == TextMode.EQUALS) {
                    try {
                        ps.add(cb.equal(root.get("id"), Long.parseLong(idStr)));
                    } catch (NumberFormatException ignored) {
                        ps.add(cb.disjunction());
                    }
                } else {
                    Expression<String> idText = root.<Long>get("id").as(String.class);
                    String pattern = (mode == TextMode.STARTS_WITH) ? idStr + "%" : "%" + idStr + "%";
                    ps.add(cb.like(idText, pattern));
                }
            }

            // sku (int) — same trick: numeric equality on EQUALS, cast-to-text on partial.
            if (c.getSku() != null && !c.getSku().isBlank()) {
                String skuStr = c.getSku().trim();
                TextMode mode = c.getSkuMode() == null ? TextMode.STARTS_WITH : c.getSkuMode();
                if (mode == TextMode.EQUALS) {
                    try {
                        ps.add(cb.equal(root.get("sku"), Integer.parseInt(skuStr)));
                    } catch (NumberFormatException ignored) {
                        ps.add(cb.disjunction());
                    }
                } else {
                    Expression<String> skuText = root.<Integer>get("sku").as(String.class);
                    String pattern = (mode == TextMode.STARTS_WITH) ? skuStr + "%" : "%" + skuStr + "%";
                    ps.add(cb.like(skuText, pattern));
                }
            }

            addTextFilter(ps, cb, root.get("description"), c.getDescription(), c.getDescriptionMode(), TextMode.CONTAINS);
            addTextFilter(ps, cb, root.get("vendor").get("name"), c.getVendor(), c.getVendorMode(), TextMode.CONTAINS);

            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(Predicate[]::new));
        };
    }

    private static void addTextFilter(List<Predicate> ps, CriteriaBuilder cb,
                                      Path<String> column, String value,
                                      TextMode mode, TextMode defaultMode) {
        if (value == null || value.isBlank()) return;
        TextMode effective = (mode == null) ? defaultMode : mode;
        String v = value.trim().toLowerCase();
        Expression<String> lower = cb.lower(column);
        switch (effective) {
            case EQUALS      -> ps.add(cb.equal(lower, v));
            case STARTS_WITH -> ps.add(cb.like(lower, v + "%"));
            case CONTAINS    -> ps.add(cb.like(lower, "%" + v + "%"));
        }
    }
    /**
     * Return a item given its id
     * @param id
     * @return
     */
    @Override
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }
    /**
     * Return a list of items by a given vendor id
     * @param vendor
     * @return
     */
    @Override
    public List<Item> findByVendor(Vendor vendor) {
        return itemRepository.findByVendor(vendor);
    }
    /**
     * Updates an existing item in the dba by a given item id
     * @param id
     * @param item
     * @return
     */
    public Item updateDescriptionAndSKUById(Long id, Item item) throws ItemNotFoundException {
        if (itemRepository.findById(id).isEmpty()) {
            throw new ItemNotFoundException();
        } else {
            Item existing = itemRepository.findById(id).get();

            existing.setDescription(item.getDescription());
            existing.setSku(item.getSku());
            return itemRepository.save(existing);
        }
    }
    /**
     * Saves a new item in the dba
     * @param item
     * @return
     */
    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }
    /**
     * Deletes a item from the dba
     * @param item
     */
    @Override
    public void delete(Item item) {
        itemRepository.delete(item);
    }
    
}
