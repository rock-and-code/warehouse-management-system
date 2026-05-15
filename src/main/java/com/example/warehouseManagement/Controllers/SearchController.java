package com.example.warehouseManagement.Controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Repositories.CustomerRepository;
import com.example.warehouseManagement.Repositories.ItemRepository;
import com.example.warehouseManagement.Repositories.VendorRepository;

/**
 * Global search backed by case-insensitive LIKE matches across the three
 * "directory" entities. Hit from the topbar form; returns a unified results page.
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    private static final int PER_CATEGORY_LIMIT = 10;

    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;

    public SearchController(ItemRepository itemRepository,
                            CustomerRepository customerRepository,
                            VendorRepository vendorRepository) {
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
        this.vendorRepository = vendorRepository;
    }

    @GetMapping
    public String search(@RequestParam(name = "q", required = false) String query, Model model) {
        String trimmed = query == null ? "" : query.trim();
        model.addAttribute("title", "Search");
        model.addAttribute("query", trimmed);

        if (trimmed.isEmpty()) {
            model.addAttribute("items", List.of());
            model.addAttribute("customers", List.of());
            model.addAttribute("vendors", List.of());
            return "search/results";
        }

        Pageable cap = PageRequest.of(0, PER_CATEGORY_LIMIT);
        List<Item> items = itemRepository.findByDescriptionContainingIgnoreCase(trimmed, cap);
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(trimmed, cap);
        List<Vendor> vendors = vendorRepository.findByNameContainingIgnoreCase(trimmed, cap);

        model.addAttribute("items", items);
        model.addAttribute("customers", customers);
        model.addAttribute("vendors", vendors);
        return "search/results";
    }
}
