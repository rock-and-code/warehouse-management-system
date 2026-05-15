package com.example.warehouseManagement.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.DTOs.AdvancedVendorSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.TextMode;
import com.example.warehouseManagement.Domains.Exceptions.VendorNotFoundException;
import com.example.warehouseManagement.Repositories.VendorRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

@Service
public class VendorServiceImpl implements VendorService {
    private final VendorRepository vendorRepository;

    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    /**
     * Returns a list of all the vendors persisted in the dba
     */
    @Override
    public Iterable<Vendor> findAll() {
        return vendorRepository.findAll();
    }

    @Override
    public Page<Vendor> findAll(Pageable pageable) {
        return vendorRepository.findAll(pageable);
    }

    @Override
    public Page<Vendor> findAdvanced(AdvancedVendorSearchCriteria criteria, Pageable pageable) {
        return vendorRepository.findAll(buildSpec(criteria), pageable);
    }

    private static Specification<Vendor> buildSpec(AdvancedVendorSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

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

            addTextFilter(ps, cb, root.get("name"),  c.getName(),  c.getNameMode(),  TextMode.CONTAINS);
            addTextFilter(ps, cb, root.get("city"),  c.getCity(),  c.getCityMode(),  TextMode.CONTAINS);
            addTextFilter(ps, cb, root.get("state"), c.getState(), c.getStateMode(), TextMode.CONTAINS);

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
     * Returns a vendor by a given id
     */
    @Override
    public Optional<Vendor> findById(Long id) {
        return vendorRepository.findById(id);
    }

    @Override
    public Vendor updateById(Long id, Vendor vendor) throws VendorNotFoundException {
        if (vendorRepository.findById(id).isEmpty()) {
            throw new VendorNotFoundException();
        } else {
            Vendor existing = vendorRepository.findById(id).get();
            //Updates fields with new values
            existing.setName(vendor.getName());
            existing.setStreet(vendor.getStreet());
            existing.setCity(vendor.getCity());
            existing.setZipcode(vendor.getZipcode());
            existing.setState(vendor.getState());
            existing.setContactInfo(vendor.getContactInfo());
            return vendorRepository.save(existing);
        }
    }

    /**
     * Persists a given vendor in the dba
     */
    @Override
    public Vendor save(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    /**
     * Deletes a vendor from the dba
     */
    @Override
    public void delete(Vendor vendor) {
        vendorRepository.delete(vendor);
    }
    
}
