package com.example.warehouseManagement.Controllers;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.ItemPrice;
import com.example.warehouseManagement.Domains.DTOs.ItemPriceDto;
import com.example.warehouseManagement.Services.ItemPriceService;
import com.example.warehouseManagement.Services.ItemService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller class for handling itemPrice operations.
 */
@Controller
@RequestMapping(value = "/")
public class ItemPriceController {

    private final ItemPriceService itemPriceService;
    private final ItemService itemService;

    private static final String ITEM_PRICE_PATH = "items/{itemId}/itemPrices";
    private static final String NEW_ITEM_PRICE_PATH = ITEM_PRICE_PATH + "/new-item-price";
    private static final String ITEM_PRICE_PATH_ID = ITEM_PRICE_PATH + "/{itemPriceId}";

    /**
     * Constructor.
     *
     * @param itemPriceService the itemPriceService to use
     */
    public ItemPriceController(ItemPriceService itemPriceService, ItemService itemService) {
        this.itemPriceService = itemPriceService;
        this.itemService = itemService;
    }

    
    //TODO: ADD FROM TO UPDATE ITEM PRICE


    /**
     * GET /items/{itemId}/itemPrices/new-item-price
     *
     * Displays a form for creating a new itemPrice.
     *
     * @param itemPrice a itemPrice object to be populated with form data
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = NEW_ITEM_PRICE_PATH)
    public String newitemPrice(@PathVariable(name = "itemId", required = false) Long itemId, Model model) {
        model.addAttribute("item", itemService.findById(itemId).get());
        model.addAttribute("itemPriceDto", new ItemPriceDto());
        return "itemPrices/newItemPriceForm";
    }

      /**
     * POST /itemPrice prices/new-itemPrice?save
     *
     * Saves the itemPrice.
     *
     * @param itemPrice a itemPrice object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the itemPrice prices
     *         list page if the itemPrice is saved successfully
     */
    @PostMapping(value = NEW_ITEM_PRICE_PATH)
    public String saveItemPrice(@PathVariable(name = "itemId", required = false) Long itemId,
        @PathVariable(name = "itemPriceId", required = false) Long itemPriceId, 
        @ModelAttribute ItemPriceDto itemPriceDto,
            HttpServletRequest request, Model model) {
        Optional<Item> item = itemService.findById(itemId);
        if (item.isEmpty()) {
            return "redirect:/items?notFound"; // Redirect to the list of items with an error message.
        }
        ItemPrice itemPrice = ItemPrice.builder()
            .item(item.get()).start(LocalDate.parse(itemPriceDto.getStartDate()))
            .end(LocalDate.parse(itemPriceDto.getEndDate())).price(itemPriceDto.getPrice()).build();
        itemPriceService.save(itemPrice);
        // Redirect to the itemPrice list page if the itemPrice is saved
        // successfully.
        return "redirect:/items/{itemId}?added";
    }

    /**
     * Handles GET requests for retrieving item details by ID.
     *
     * @param id the itemPriceId
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of item prices if the item price is not found
     */
    @GetMapping(value = ITEM_PRICE_PATH_ID)
    public String getItemPriceDetails(@PathVariable(name = "itemId", required = false) Long itemId,
        @PathVariable(name = "itemPriceId", required = false) Long itemPriceId, Model model) {
        Optional<ItemPrice> itemPrice = itemPriceService.findById(itemPriceId);
        // Checking if the itemPrice exists
        if (itemPrice.isEmpty())
            return String.format("redirect:/items/%s",itemId); // Redirecting to the list of item details

        // Adding the itemPrice and a title attribute to the model
        model.addAttribute("itemPrice", itemPrice.get());
        model.addAttribute("title", "Item Price Details");
        return "itemPrices/itemPriceDetails"; // Returning the view template name
    }

    /**
     * Handles POST requests for deleting a itemPrice by ID.
     *
     * @param id the itemPrice ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the item detail screen if the item price is not found or cannot be deleted
     */
    @PostMapping(value = ITEM_PRICE_PATH_ID, params = "delete")
    public String deleteItemPrice(@PathVariable(name = "itemId", required = false) Long itemId,
        @PathVariable(name = "itemPriceId", required = false) Long itemPriceId, Model model) {
        Optional<ItemPrice> itemPrice = itemPriceService.findById(itemPriceId);
         // Check if the item price exists.
         if (itemPrice.isEmpty()) {
            return String.format("redirect:/items/%d?notFound",itemId); // Redirect to the item screen with an error message.
        }

        // Check if the itemPrice has associated saless order lines since an itemPrice 
        // has to be included in a sales order line before to sell it to a customer, thus is an itemPrice
        // has been sold we should not deleted unless DBA and management agree to it.
        if (itemPrice.get().getSalesOrderLine().size() > 0) {
            return String.format("redirect:/items/%d?failedToDelete",itemId); // Redirect to the item details screen with an error message.
        }

        // Delete the itemPrice and redirect to the item details screen with a success message.
        itemPriceService.delete(itemPrice.get());
        return String.format("redirect:/items/%d?deleted",itemId); // Redirect to the list of item prices with succesful deletion message.
    }
}
