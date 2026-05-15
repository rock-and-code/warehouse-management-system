package com.example.warehouseManagement.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.DTOs.AdvancedCustomerSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.TextMode;
import com.example.warehouseManagement.Domains.Exceptions.CustomerNotFoundException;
import com.example.warehouseManagement.Repositories.CustomerRepository;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;


    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Iterable<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Page<Customer> findAdvanced(AdvancedCustomerSearchCriteria criteria, Pageable pageable) {
        return customerRepository.findAll(buildSpec(criteria), pageable);
    }

    /**
     * AND-combine every populated filter. Empty / null fields drop out;
     * empty predicate list ⇒ cb.conjunction() so the page returns all rows.
     */
    private static Specification<Customer> buildSpec(AdvancedCustomerSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (c.getId() != null && !c.getId().isBlank()) {
                String idStr = c.getId().trim();
                TextMode mode = c.getIdMode() == null ? TextMode.STARTS_WITH : c.getIdMode();
                if (mode == TextMode.EQUALS) {
                    try {
                        ps.add(cb.equal(root.get("id"), Long.parseLong(idStr)));
                    } catch (NumberFormatException ignored) {
                        ps.add(cb.disjunction()); // un-parseable id → no match
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

    /**
     * Append a case-insensitive predicate on a string column when the user
     * provided a non-blank value. Defaults to {@code defaultMode} if the
     * criteria didn't specify an explicit mode.
     */
    private static void addTextFilter(List<Predicate> ps,
                                      jakarta.persistence.criteria.CriteriaBuilder cb,
                                      jakarta.persistence.criteria.Path<String> column,
                                      String value,
                                      TextMode mode,
                                      TextMode defaultMode) {
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

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer updateById(Long id, Customer customer) throws CustomerNotFoundException {
        if (customerRepository.findById(id).isEmpty()) {
            throw new CustomerNotFoundException();
        } else {
            Customer existing = customerRepository.findById(id).get();
            //Updates fields with new values
            existing.setName(customer.getName());
            existing.setStreet(customer.getStreet());
            existing.setCity(customer.getCity());
            existing.setZipcode(customer.getZipcode());
            existing.setState(customer.getState());
            existing.setContactInfo(customer.getContactInfo());
            return customerRepository.save(existing);
        }
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }
    
}
