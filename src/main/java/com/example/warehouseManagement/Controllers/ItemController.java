package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.StatePool;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.DTOs.ItemDto;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.VendorService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller class for handling item operations.
 */
@Controller
@RequestMapping(value = "/")
public class ItemController {

    private final ItemService itemService;
    private final VendorService vendorService;

    private static final String ITEM_PATH = "items";
    private static final String NEW_ITEM_PATH = ITEM_PATH + "/new-item";
    private static final String ITEM_PATH_ID = ITEM_PATH + "/{itemId}";

    /**
     * Constructor.
     *
     * @param itemService the itemService to use
     */
    public ItemController(ItemService itemService, VendorService vendorService) {
        this.itemService = itemService;
        this.vendorService = vendorService;
    }

    //TODO: ADD FORM TO LIST ITEMS BY VENDOR ID
    //TODO: ADD FORM TO UPDATE ITEM INFORMATION

    /**
     * Handles GET requests for retrieving a list of items.
     *
     * @param model the Model to populate with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = ITEM_PATH)
    public String getItems(Model model) {
        // Adding a list of items and a title attribute to the model
        model.addAttribute("items", itemService.findAll());
        model.addAttribute("title", "Items");
        return "items/items"; // Returning the view template name
    }


    /**
     * GET /items/new-item
     *
     * Displays a form for creating a new item.
     *
     * @param item a item object to be populated with form data
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = NEW_ITEM_PATH)
    public String newItem(@ModelAttribute ItemDto itemDto, Model model) {
        model.addAttribute("states", StatePool.getStates());
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("itemDto", new ItemDto());
        return "items/newItemForm";
    }

      /**
     * POST /items/new-item?save
     *
     * Saves the item.
     *
     * @param item a item object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the items
     *         list page if the item is saved successfully
     */
    @PostMapping(value = NEW_ITEM_PATH)
    public String saveItem(@ModelAttribute ItemDto itemDto,
            HttpServletRequest request, Model model) {
        Optional<Vendor> vendor = vendorService.findById(itemDto.getVendorId());
        Item newItem = Item.builder().vendor(vendor.get())
            .sku(itemDto.getSku()).description(itemDto.getDescription()).build();
        itemService.save(newItem);
        // Redirect to the item list page if the item is saved
        // successfully.
        return "redirect:/items?added";
    }

    /**
     * Handles GET requests for retrieving item details by ID.
     *
     * @param id the item ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of items if the item is not found
     */
    @GetMapping(value = ITEM_PATH_ID)
    public String getItemDetails(@PathVariable(name = "itemId", required = false) Long id, Model model) {
        Optional<Item> item = itemService.findById(id);
        // Checking if the item exists
        if (item.isEmpty())
            return "redirect:/items"; // Redirecting to the list of items

        // Adding the item and a title attribute to the model
        model.addAttribute("item", item.get());
        model.addAttribute("title", "item Details");
        return "items/itemDetails"; // Returning the view template name
    }

    /**
     * Handles POST requests for deleting a item by ID.
     *
     * @param id the item ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of items if the item is not found or cannot be deleted
     */
    @PostMapping(value = ITEM_PATH_ID, params = "delete")
    public String deleteItem(@PathVariable(name = "itemId", required = false) Long id, Model model) {
        Optional<Item> item = itemService.findById(id);
         // Check if the item exists.
         if (item.isEmpty()) {
            return "redirect:/items?notFound"; // Redirect to the list of items with an error message.
        }

        // Check if the item has associated goods receipt note lines since an item first
        // has to be received in a warehouse before sell it to customer, thus is an item
        // has been received we should not deleted unless DBA and management agree to it.
        if (item.get().getGoodsReceiptNoteLines().size() > 0) {
            return "redirect:/items?failedToDelete"; // Redirect to the list of items with a failure message.
        }

        // Delete the item and redirect to the list of items with a success message.
        itemService.delete(item.get());
        return "redirect:/items/deleted";
    }
}
