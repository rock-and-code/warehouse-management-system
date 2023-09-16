package com.example.warehouseManagement.Bootstrap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.CustomerPool;
import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNote.GrnStatus;
import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;
import com.example.warehouseManagement.Domains.Invoice;
import com.example.warehouseManagement.Domains.InvoiceLine;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.ItemCost;
import com.example.warehouseManagement.Domains.ItemPool;
import com.example.warehouseManagement.Domains.ItemPrice;
import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJob.PjStatus;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrder.PoStatus;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.SalesOrder.SoStatus;
import com.example.warehouseManagement.Domains.SalesOrderLine;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.VendorPool;
import com.example.warehouseManagement.Domains.Warehouse;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.ItemInfoDto;
import com.example.warehouseManagement.Repositories.BackorderRepository;
import com.example.warehouseManagement.Repositories.CustomerRepository;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteLineRepository;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteRepository;
import com.example.warehouseManagement.Repositories.InvoiceLineRepository;
import com.example.warehouseManagement.Repositories.InvoiceRepository;
import com.example.warehouseManagement.Repositories.ItemCostRepository;
import com.example.warehouseManagement.Repositories.ItemPriceRepository;
import com.example.warehouseManagement.Repositories.ItemRepository;
import com.example.warehouseManagement.Repositories.PickingJobLineRepository;
import com.example.warehouseManagement.Repositories.PickingJobRepository;
import com.example.warehouseManagement.Repositories.PurchaseOrderLineRepository;
import com.example.warehouseManagement.Repositories.PurchaseOrderRepository;
import com.example.warehouseManagement.Repositories.SaleOrderLineRepository;
import com.example.warehouseManagement.Repositories.SalesOrderRepository;
import com.example.warehouseManagement.Repositories.StockRepository;
import com.example.warehouseManagement.Repositories.VendorRepository;
import com.example.warehouseManagement.Repositories.WarehouseRepository;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

@Component
public class Bootstrap implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final VendorRepository vendorRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SaleOrderLineRepository saleOrderLineRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseSectionRepository warehouseSectionRepository;
    private final StockRepository stockRepository;
    private final GoodsReceiptNoteRepository goodsReceiptNoteRepository;
    private final GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemCostRepository itemCostRepository;
    private final PickingJobRepository pickingJobRepository;
    private final PickingJobLineRepository pickingJobLineRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final BackorderRepository backorderRepository;

    public Bootstrap(CustomerRepository customerRepository, ItemRepository itemRepository,
            VendorRepository vendorRepository, SalesOrderRepository salesOrderRepository,
            PurchaseOrderRepository purchaseOrderRepository, SaleOrderLineRepository saleOrderLineRepository,
            PurchaseOrderLineRepository purchaseOrderLineRepository, WarehouseRepository warehouseRepository,
            WarehouseSectionRepository warehouseSectionRepository, StockRepository stockRepository,
            GoodsReceiptNoteRepository goodsReceiptNoteRepository,
            GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository,
            ItemPriceRepository itemPriceRepository, ItemCostRepository itemCostRepository,
            PickingJobRepository pickingJobRepository, PickingJobLineRepository pickingJobLineRepository,
            InvoiceRepository invoiceRepository, InvoiceLineRepository invoiceLineRepository,
            BackorderRepository backorderRepository) {
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
        this.vendorRepository = vendorRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.saleOrderLineRepository = saleOrderLineRepository;
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
        this.warehouseRepository = warehouseRepository;
        this.warehouseSectionRepository = warehouseSectionRepository;
        this.stockRepository = stockRepository;
        this.goodsReceiptNoteRepository = goodsReceiptNoteRepository;
        this.goodsReceiptNoteLineRepository = goodsReceiptNoteLineRepository;
        this.itemPriceRepository = itemPriceRepository;
        this.itemCostRepository = itemCostRepository;
        this.pickingJobRepository = pickingJobRepository;
        this.pickingJobLineRepository = pickingJobLineRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.backorderRepository = backorderRepository;
    }

    /**
     * Generates dummy data for testing purposes
     */

    @Override
    public void run(String... args) throws Exception {

        // Variables
        Random random = new Random();
        int VENDORS = 5, CUSTOMERS = 10, SALES_ORDER = 20, PURCHASE_ORDER = 30;
        List<Vendor> savedVendors = new ArrayList<>();
        List<Customer> savedCustomers = new ArrayList<>();
        List<Item> savedItems = new ArrayList<>();
        Queue<PurchaseOrder> receivedPurchaseOrders = new LinkedList<>();

        // Generate 5 random vendors and adds them to the vendors repository
        // TC(N)
        for (int i = 0; i < VENDORS; i++) {
            Vendor vendor = VendorPool.vendorList.get(i);
            vendor.setPurchaseOrders(new ArrayList<>());
            Vendor savedVendor = vendorRepository.save(vendor);
            savedVendors.add(savedVendor);
        }
        // VENDORS: 0-APPLE, 1-MICROSOFT, 2-ALPHABET, 3-AMAZON, 4-CISCO

        // Generate 40 random products and adds them to the product repository
        // 0-7 APPLE 8-10 MICROSOFT 11-16 ALPABHET 17-33 AMAZON 34-40 CISCO
        int[] vendorsItems = { 7, 10, 16, 33, 40 };

        // T(XN)
        // Adding products to vendors
        for (int i = 0; i < VENDORS; i++) {
            int start = (i == 0) ? 0 : vendorsItems[i - 1] + 1; // FIX
            Vendor currentVendor = savedVendors.get(i);
            for (int j = start; j <= vendorsItems[i]; j++) {
                ItemInfoDto itemInfo = ItemPool.ProductList.get(j);
                Item item = Item.builder().description(itemInfo.getDescription()).sku(itemInfo.getSku()).build();
                // Add vendos to the savedProducts objects
                item.setVendor(currentVendor);
                Item savedItem = itemRepository.save(item);
                ItemPrice itemPrice = ItemPrice.builder().start(LocalDate.of(2023, 1, 1))
                        .salesOrderLine(new ArrayList<>())
                        .end(LocalDate.of(2023, 12, 31)).price(itemInfo.getSalesPrice()).item(savedItem).build();
                ItemCost itemCost = ItemCost.builder().start(LocalDate.of(2023, 1, 1))
                        .purchaseOrderLine(new ArrayList<>(i))
                        .end(LocalDate.of(2023, 12, 31)).cost(itemInfo.getCost()).item(savedItem).build();
                ItemPrice savedItemPrice = itemPriceRepository.save(itemPrice);
                ItemCost savedItemCost = itemCostRepository.save(itemCost);
                // salesPrices.add(savedItemPrice);
                // costs.add(savedItemCost);
                savedItem.getItemPrices().add(savedItemPrice);
                savedItem.getItemCosts().add(savedItemCost);

                savedItems.add(savedItem);
                // Adds products to the savedVendors objects
                currentVendor.getItems().add(savedItem);
            }
        }

        // Generate 10 random customers and adds them to the customer repository
        // TC(M)
        for (int i = 0; i < CUSTOMERS; i++) {
            Customer customer = CustomerPool.customerList.get(i);
            customer.setSalesOrders(new ArrayList<>());
            Customer savedCustomer = customerRepository.save(customer);
            savedCustomers.add(savedCustomer);
        }

        for (Vendor vendor : savedVendors) {
            for (int i = 0; i < PURCHASE_ORDER; i++) {
                int month = random.nextInt(1, 12);
                int day = (month == 2) ? random.nextInt(1, 28) : random.nextInt(1, 30);
                int year = 2023;
                LocalDate date = LocalDate.of(year, month, day);
                int purchaseOrderLines = random.nextInt(3, 20);
                int orderStatus = random.nextInt(3);
                // adds savedCustomer to the purchase order
                PurchaseOrder purchaseOrder = PurchaseOrder.builder().vendor(vendor).date(date)
                        .purchaseOrderLines(new ArrayList<>())
                        .status((orderStatus == 0 || orderStatus == 2) ? PoStatus.RECEIVED : PoStatus.IN_TRANSIT)
                        .goodsReceiptNotes(new ArrayList<>()).build();

                // Adds the purchase order to the purchase order repository
                PurchaseOrder po = purchaseOrderRepository.save(purchaseOrder);

                PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(po);

                // Each purchase order will generate between 3-20 sales order lines
                for (int j = 0; j < purchaseOrderLines; j++) {
                    int randItem = random.nextInt(savedItems.size() - 1);
                    int qty = random.nextInt(1, 6);
                    Item selectedItem = savedItems.get(randItem);
                    // adds savedProduct to the purchaseOrder line
                    // Adds the savedPurchaseOrder to the purchase order line
                    PurchaseOrderLine purchaseOrderLine = PurchaseOrderLine.builder().item(selectedItem).qty(qty)
                            .purchaseOrder(savedPurchaseOrder).itemCost(selectedItem.getItemCosts().get(0)).build();
                    // Adds the purchase orders lines to the purchase orders lines repository
                    PurchaseOrderLine savedPurchaseOrderLine = purchaseOrderLineRepository.save(purchaseOrderLine);
                    // Adds the purchase orders lines to the savedPurchaseOrder
                    savedPurchaseOrder.getPurchaseOrderLines().add(savedPurchaseOrderLine);
                }
                // Adds the purchase order to vendor
                vendor.getPurchaseOrders().add(savedPurchaseOrder);

                // Adds the saved purchase order to the po queue
                if (savedPurchaseOrder.getStatus() == PoStatus.RECEIVED)
                    receivedPurchaseOrders.offer(savedPurchaseOrder);
                else {
                    // Adding pending GRN
                    GoodsReceiptNote goodsReceiptNote = GoodsReceiptNote.builder().date(date)
                            .purchaseOrder(savedPurchaseOrder).date(savedPurchaseOrder.getDate()).status(GrnStatus.PENDING).build();
                    GoodsReceiptNote savedGoodsReceiptNote = goodsReceiptNoteRepository.save(goodsReceiptNote);

                    for (PurchaseOrderLine pol : savedPurchaseOrder.getPurchaseOrderLines()) {
                        GoodsReceiptNoteLine goodsReceiptNoteLine = GoodsReceiptNoteLine.builder()
                                .goodsReceiptNote(savedGoodsReceiptNote)
                                .qty(pol.getQty()).item(pol.getItem()).build();
                        GoodsReceiptNoteLine savedGoodsReceiptNoteLine = goodsReceiptNoteLineRepository
                                .save(goodsReceiptNoteLine);
                        savedGoodsReceiptNote.getGoodsReceiptNoteLines().add(savedGoodsReceiptNoteLine);
                        savedPurchaseOrder.getGoodsReceiptNotes().add(savedGoodsReceiptNote);
                        //purchaseOrderRepository.save(savedPurchaseOrder);
                    }
                }
            }
        }

        // Warehouse, warehouse sections, stocks levels
        // Warehouse labelling system source:
        // https://idlabelinc.com/tips-for-effective-warehouse-numbering-schemes/
        // https://docs.flexsim.com/en/19.2/ConnectingFlows/Warehousing/KeyConceptsWarehousing/KeyConceptsWarehousing.html
        // RACKS LEVELS A-B-C-D-E [5 LEVELS]

        int LEVELS = 6, SLOTS = 2, BAYS = 8, AISLES = 12; // [1152]
        // AA-01-1-A [AISLE, BAY, SLOT, LEVEL]
        List<String> aisles = List.of("AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AM");
        List<String> bays = List.of("01", "02", "03", "04", "05", "06", "07", "08");
        List<String> levels = List.of("A", "B", "C", "D", "E", "F");

        Warehouse warehouse = Warehouse.builder().address("350 County Road, Jersey City, NJ 07307").build();
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        List<WarehouseSection> savedWarehouseSections = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        // int sample = 50, counter = 0;
        // Warehouse section label example: "AA-01-1-A" (AISLE-BAY-SLOT-LEVEL)
        builder.append("AA-01-1-A");
        for (int i = 0; i < AISLES; i++) {
            // AISLES
            builder.replace(0, 2, aisles.get(i));
            for (int j = 0; j < BAYS; j++) {
                // BAYS
                builder.replace(3, 5, bays.get(j));
                for (int k = 0; k < SLOTS; k++) {
                    // SLOTS
                    builder.replace(6, 7, String.valueOf(k));
                    for (int l = 0; l < LEVELS; l++) {
                        // LEVELS
                        builder.replace(8, 9, levels.get(l));
                        String section = builder.toString();
                        WarehouseSection warehouseSection = WarehouseSection.builder().sectionNumber(section)
                                .warehouse(savedWarehouse).build();
                        WarehouseSection savedWarehouseSection = warehouseSectionRepository.save(warehouseSection);
                        savedWarehouseSections.add(savedWarehouseSection);
                        // if (counter <= sample) {
                        // System.out.println(section);
                        // counter++;
                        // }
                    }
                }
            }
        }
        // FLOOR SECTIONS USUALLY USED BY RECEIVING DEPARTMENT TO PLACE THE MERCHADISE
        // BEFORE PUTTING IT AWAY IN A SPECIFIC WH SECTION
        WarehouseSection floor = WarehouseSection.builder().sectionNumber("00-00-0-0").warehouse(savedWarehouse)
                .build();
        // PICKING SECTIONS USUALLY USED TO PLACE THE MERCHADISED PICKED TO BE DELIVERED
        // TO A CUSTOMER FOR THE ACCOUNTING DEPARTEMENT TO BILL
        // WarehouseSection picking =
        // WarehouseSection.builder().sectionNumber("11-11-1-1").warehouse(savedWarehouse).build();

        WarehouseSection savedFloorSection = warehouseSectionRepository.save(floor);
        // WarehouseSection savedPickingSection =
        // warehouseSectionRepository.save(picking);

        savedWarehouse.getSections().add(savedFloorSection);
        // savedWarehouse.getSections().add(savedPickingSection);

        // adds Goods receipt notes, goods receipt notes lines to add stock levels in
        // the warehouse
        int WAREHOUSE_SECTIONS = savedWarehouseSections.size();

        while (!receivedPurchaseOrders.isEmpty()) {
            PurchaseOrder rPurchaseOrder = receivedPurchaseOrders.poll();
            int plusDays = random.nextInt(30, 90);
            LocalDate date = rPurchaseOrder.getDate().plusDays(plusDays);
            GoodsReceiptNote goodsReceiptNote = GoodsReceiptNote.builder().date(date).purchaseOrder(rPurchaseOrder)
                    .date(date).status(GrnStatus.RECEIVED).build();
            GoodsReceiptNote grn = goodsReceiptNoteRepository.save(goodsReceiptNote);
            GoodsReceiptNote savedGoodsReceiptNote = goodsReceiptNoteRepository.save(grn);

            for (PurchaseOrderLine pol : rPurchaseOrder.getPurchaseOrderLines()) {
                GoodsReceiptNoteLine goodsReceiptNoteLine = GoodsReceiptNoteLine.builder()
                        .goodsReceiptNote(savedGoodsReceiptNote)
                        .qty(pol.getQty()).item(pol.getItem()).build();
                GoodsReceiptNoteLine savedGoodsReceiptNoteLine = goodsReceiptNoteLineRepository
                        .save(goodsReceiptNoteLine);
                savedGoodsReceiptNote.getGoodsReceiptNoteLines().add(savedGoodsReceiptNoteLine);
                rPurchaseOrder.getGoodsReceiptNotes().add(savedGoodsReceiptNote);
                WarehouseSection selectedWarehouseSection = savedWarehouseSections
                        .get(random.nextInt(WAREHOUSE_SECTIONS));
                Stock newStock = Stock.builder().qtyOnHand(savedGoodsReceiptNoteLine.getQty())
                        .item(savedGoodsReceiptNoteLine.getItem())
                        .warehouseSection(selectedWarehouseSection).build();
                stockRepository.save(newStock);
            }
        }

        // Generate 20 sales orders by each savedCustomer objects
        // TC(MSO)
        for (Customer customer : savedCustomers) {
            for (int i = 0; i < SALES_ORDER; i++) {
                int month = random.nextInt(1, 12);
                int day = (month == 2) ? random.nextInt(1, 28) : random.nextInt(1, 30);
                int year = 2023;
                LocalDate date = LocalDate.of(year, month, day);
                int salesOrderLines = random.nextInt(3, 20);
                int orderStatus = random.nextInt(3);
                // adds savedCustomer to the savedSalesOrder
                SalesOrder salesOrder = SalesOrder.builder().customer(customer).date(date)
                        .saleOrderLines(new ArrayList<>())
                        .status((orderStatus == 0 || orderStatus == 2) ? SoStatus.PENDING : SoStatus.SHIPPED).build();

                // Adds the sales order to the sales order repository
                SalesOrder so = salesOrderRepository.save(salesOrder);

                SalesOrder savedSalesOrder = salesOrderRepository.save(so);

                // Each sales order will generate between 3-20 sales order lines
                for (int j = 0; j < salesOrderLines; j++) {
                    int randItem = random.nextInt(savedItems.size() - 1);
                    int qty = random.nextInt(1, 6);
                    Item selectedItem = savedItems.get(randItem);
                    // adds savedProduct to the salesOrderLines
                    // Adds the savedSaleOrder to the savedSalesOrderLines
                    SalesOrderLine saleOrderLine = SalesOrderLine.builder().item(selectedItem).qty(qty)
                            .salesOrder(savedSalesOrder).itemPrice(selectedItem.getItemPrices().get(0)).build();
                    // Adds the sales orders lines to the sales orders lines repository
                    SalesOrderLine savedSaleOrderLine = saleOrderLineRepository.save(saleOrderLine);
                    // Adds the sales orders lines to the savedSalesOrder
                    savedSalesOrder.getSaleOrderLines().add(savedSaleOrderLine);
                }
                // Adds the sales order to customer
                customer.getSalesOrders().add(savedSalesOrder);

                PickingJob pickingJob = PickingJob.builder().salesOrder(savedSalesOrder).date(savedSalesOrder.getDate())
                        .build();
                PickingJob savedPickingJob = pickingJobRepository.save(pickingJob);
                if (savedSalesOrder.getStatus() == SoStatus.SHIPPED)
                    pickingJob.setStatus(PjStatus.FULFILLED);
                for (SalesOrderLine salesOrderLine : savedSalesOrder.getSaleOrderLines()) {
                    PickingJobLine pickingJobLine = PickingJobLine.builder().item(salesOrderLine.getItem())
                            .qtyToPick(salesOrderLine.getQty()).pickingJob(savedPickingJob).build();
                    PickingJobLine savedPickingJobLine = pickingJobLineRepository.save(pickingJobLine);
                    savedPickingJob.getPickingJobLines().add(savedPickingJobLine);
                }
                pickingJobRepository.save(savedPickingJob);

                // TODO -> ADD INVOICES FOR FULFILLED PICKING JOBS

                // Substracting stocks levels for sales orders marked as shipped
                if (savedSalesOrder.getStatus() == SoStatus.SHIPPED) {
                    Invoice invoice = Invoice.builder().date(savedSalesOrder.getDate().plusDays(random.nextLong(1, 5)))
                            .customer(savedSalesOrder.getCustomer()).salesOrder(savedSalesOrder).build();
                    Invoice savedInvoice = invoiceRepository.save(invoice);
                    for (SalesOrderLine sol : savedSalesOrder.getSaleOrderLines()) {
                        int ordered = sol.getQty();
                        int picked = 0;
                        List<Stock> stocks = stockRepository.findByItem(sol.getItem());
                        for (Stock stock : stocks) {
                            if (stock.getQtyOnHand() > ordered) {
                                stock.setQtyOnHand(stock.getQtyOnHand() - ordered);
                                stockRepository.save(stock);
                                picked += ordered;
                                break;
                            } else {
                                ordered -= stock.getQtyOnHand();
                                picked += stock.getQtyOnHand();
                                stockRepository.delete(stock);
                            }
                        }
                        InvoiceLine invoiceLine = InvoiceLine.builder().invoice(savedInvoice).item(sol.getItem())
                                .qty(picked).build();
                        InvoiceLine savedInvoiceLine = invoiceLineRepository.save(invoiceLine);
                        savedInvoice.getInvoiceLines().add(savedInvoiceLine);
                        // Recording backorders
                        if (ordered > 0) {
                            Backorder backorder = Backorder.builder().qty(ordered).item(sol.getItem())
                                    .salesOrder(sol.getSalesOrder()).build();
                            Backorder savedBackorder = backorderRepository.save(backorder);
                            savedSalesOrder.getBackorders().add(savedBackorder);
                            salesOrderRepository.save(savedSalesOrder);
                        }

                    }
                    invoiceRepository.save(savedInvoice);
                }
            }
        }

        // PRINTS customer, vendor, sales order, sales orders lines, products repository
        // lines
        System.out.printf("Customers: %d\n", customerRepository.count());
        System.out.printf("Vendors: %d\n", vendorRepository.count());
        System.out.printf("Sales Order: %d\n", salesOrderRepository.count());
        System.out.printf("Sales Order Lines: %d\n", saleOrderLineRepository.count());
        System.out.printf("Purchase Order: %d\n", purchaseOrderRepository.count());
        System.out.printf("Purchase Order Lines: %d\n", purchaseOrderLineRepository.count());
        System.out.printf("Items: %d\n", itemRepository.count());
        System.out.printf("Warehouse Sections: %d\n", warehouseSectionRepository.count());
        System.out.printf("Goods Receipt Notes: %d\n", goodsReceiptNoteRepository.count());
        System.out.printf("Stock: %d\n", stockRepository.count());
        System.out.printf("Picking Jobs: %d\n", pickingJobRepository.count());
        System.out.printf("Invocies: %d\n", invoiceRepository.count());
        System.out.printf("Backorders: %d\n", backorderRepository.count());

    }

}
