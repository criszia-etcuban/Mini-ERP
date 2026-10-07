import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import  java.util.List;

public class Main {

    public static Item findItemBySku(HashMap<String, Item> itemMap, String sku) throws ItemNotFoundException {
        Item item = itemMap.get(sku);
        if (item == null) {
            throw new ItemNotFoundException("No item found with SKU: " + sku);
        }
        return item;
    }
    public static void main(String[] args) {

        /*Generics*/
        //Suppliers
        Repository<Supplier> supplierRepo = new Repository<>();
        supplierRepo.add("S001", new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com"));
        supplierRepo.add("S002", new Supplier("S002", "OfficeWorld Supply", "0917-888-2222", "officeworld@email.com"));

        //Items
        Repository<Item> itemRepo = new Repository<>();
        itemRepo.add("A001", new Item("A001", "Wireless Mouse", 100, 0.20, 100, 15, 100, 1, supplierRepo.findById("S001")));
        itemRepo.add("A002", new  Item("A002", "Mechanical Keyboard", 1500, 0.20, 40, 10, 50, 1, supplierRepo.findById("S001")));
        itemRepo.add("A003", new Item("A003", "Office Chair", 3500, 0.20, 15, 5, 20, 2, supplierRepo.findById("S002")));

        System.out.println("=== ALL SUPPLIERS (via getAll) ===");
        List<Supplier> allSuppliers = supplierRepo.getAll();
        for (Supplier s : allSuppliers) {
            System.out.println(s.getSupplierId() + " - " + s.getSupplierName());
        }
        System.out.println("==============================");

        System.out.println("=== ALL ITEMS (via getAll) ===");
        List<Item> allItems = itemRepo.getAll();
        for (Item i : allItems) {
            System.out.println(i.getItemCode() + " - " + i.getItemName() + " | Stock: " + i.getStock());
        }
        System.out.println("==============================");

        Item found = itemRepo.findById("A001");
        System.out.println("Found: " + found.getItemName());

        Supplier foundSupplier = supplierRepo.findById("S001");
        System.out.println("Found: " + foundSupplier.getSupplierName());
        System.out.println("==============================");

        System.out.println("Total items: " + itemRepo.count());
        System.out.println("Total suppliers: " + supplierRepo.count());

        // ArrayList of Suppliers
//        ArrayList<Supplier> suppliers = new ArrayList<>();
//        suppliers.add(new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com"));
//        suppliers.add(new Supplier("S002", "OfficeWorld Supply", "0917-888-2222", "officeworld@email.com"));

        // ArrayList of Items
//        ArrayList<Item> items = new ArrayList<>();
//        items.add(new Item("A001", "Wireless Mouse", 100, 0.20, 100, 15, 100, 1, suppliers.get(0)));
//        items.add(new Item("A002", "Mechanical Keyboard", 1500, 0.20, 40, 10, 50, 1, suppliers.get(0)));
//        items.add(new Item("A003", "Office Chair", 3500, 0.20, 15, 5, 20, 2, suppliers.get(1)));

        // === HashMap: SKU code -> Item, para sa mabilis na lookup ===
/*        HashMap<String, Item> itemMap = new HashMap<>();
        for (Item item : items) {
            itemMap.put(item.getItemCode(), item);
        }
        HashMap<String, Supplier> supplierMap = new HashMap<>();
        for (Supplier supplier : suppliers) {
            supplierMap.put(supplier.getSupplierId(), supplier);
        }*/

/*
        System.out.println("=== HASHMAP LOOKUP ===");
        Item lookup = itemMap.get("A001");
        if (lookup != null) {
            System.out.println("Found: " + lookup.getItemName() + " | Stock: " + lookup.getStock());
        }
        // Kung walang SKU na ganun
        Item notFound = itemMap.get("A999");
        if (notFound == null) {
            System.out.println("A999 not found in inventory.");
        }
        System.out.println("==============================");

        // === TreeMap: parehong Map, pero naka-sort ayon sa key (SKU code) ===
        TreeMap<String, Item> sortedItemMap = new TreeMap<>(itemMap);

        System.out.println("=== TREEMAP (sorted by SKU) ===");
        for (Map.Entry<String, Item> entry : sortedItemMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().getItemName());
        }

        System.out.println("==============================");

        TreeMap<String, Supplier> sortedSupplierMap = new TreeMap<>(supplierMap) ;
        System.out.println("Supplier");
        for (Map.Entry<String, Supplier> entry : sortedSupplierMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().getSupplierName());
        }
        System.out.println("==============================");

        // === HashSet: unique na category codes, walang duplicate ===
        HashSet<Integer> categoryCodesUsed = new HashSet<>();
        for (Item item : items) {
            categoryCodesUsed.add(item.getCategoryCode());
        }

        System.out.println("=== UNIQUE CATEGORY CODES ===");
        System.out.println(categoryCodesUsed);   // hal. [1, 2], walang paulit-ulit kahit 2 item ang category 1

        // === Set operations: pagsuri kung kasama na ===
        if (categoryCodesUsed.contains(1)) {
            System.out.println("May items sa category 1 (Electronics/Accessories).");
        }
        System.out.println("==============================");

        Item item = items.get(0);

        System.out.println("Item: " + item.getItemCode() + " - " + item.getItemName() + " (" + item.getCategoryName() + ")");
        System.out.println("Supplier: " + item.getSupplier().getSupplierName() + " | " + item.getSupplier().getContactNumber() + " | " + item.getSupplier().getEmail());
        System.out.println("Unit Price: " + item.getUnitPrice() + " | " + "Stock: " + item.getStock() + " pcs");
        System.out.println("Amount: ₱ " + item.priceWithTax());
        System.out.println("==============================");

        try {
            //loop method
            for (int day = 1; day <= 5; day++) {
                int order = 30;

//                if (order > item.getStock()) {
//                    System.out.println("Day " + day + ": Not enough stock for order of " + order);
//                    break;
//                }

                double total = item.calculateTotal(order);
                item.sell(order);

                System.out.println("Day " + day + ": sold " + order
                        + " | total ₱" + total
                        + " | stock " + item.getStock()
                        + " | " + item.getStockStatus());

                int reorderQty = item.calculateReorderQty();
                if (reorderQty > 0) {
                    System.out.println("   -> ALERT!: Reorder " + reorderQty + " pcs from " + item.getSupplier().getSupplierName());
                }
            }
        } catch (InsufficientStockException e) {
            System.out.println("STOPPED: " + e.getMessage());
        }
        System.out.println("==============================");

        System.out.println("Stock left: " + item.getStock() + " : " + item.getStockStatus());
        System.out.println("==============================");
        System.out.println("=== INVENTORY ===");
        for (Item item1 : items) {
            System.out.println(item1.getItemCode() + " - " + item1.getItemName()
                    + " (" + item1.getCategoryName() + ")"
                    + " | Stock: " + item1.getStock()
                    + " | " + item1.getStockStatus()
                    + " | Supplier: " + item1.getSupplier().getSupplierName());
        }
        System.out.println("==============================");

//        // New: PurchaseOrder at IApprovable
//        Order po1 = new PurchaseOrder("PO-001", "2026-09-30", mouse, 50);
//        po1.printSummary();
//
//        if (po1 instanceof IApprovable approvable) {
//            System.out.println("Status: " + approvable.getApprovalStatus());
//            approvable.approve("Manager Cruz");
//            System.out.println("Status: " + approvable.getApprovalStatus());
//        }

        // LinkedList of PurchaseOrders, same List interface, magkaiba lang sa loob
        LinkedList<PurchaseOrder> purchaseOrders = new LinkedList<>();
        System.out.println("=== PURCHASE ORDERS ===");
        try {
            purchaseOrders.add(new PurchaseOrder("PO-001", "2026-10-01", itemMap.get("A001"), 20));
            purchaseOrders.add(new PurchaseOrder("PO-002", "2026-10-02", itemMap.get("A003"), - 5));
            //sample data for not found
            Item item3 = findItemBySku(itemMap, "A999");
            purchaseOrders.add(new PurchaseOrder("PO-003", "2026-10-03", item3, 10));
        } catch (ItemNotFoundException e) {
            System.out.println("Error creating PO: " + e.getMessage());
        } catch (InvalidPurchaseOrderException e ) {
            System.out.println("ERROR! (Invalid PO): " + e.getMessage());
        }
        for (PurchaseOrder po : purchaseOrders) {
            po.printSummary();
            System.out.println("   Status: " + po.getApprovalStatus());
            po.approve("Manager Cruz");
            System.out.println("Status: " + po.getApprovalStatus());
        }

//        System.out.println("==============================");
//
//        Order[] orders = {
//                new PurchaseOrder("PO-002", "2026-10-01", mouse, 20),
//                new PurchaseOrder("PO-003", "2026-10-02", mouse, 5)
//        };
//
//        System.out.println("Orders");
//        for (Order order : orders) {
//            order.printSummary();
//        }

        System.out.println("==============================");

        // Paghahanap ng specific item (common List operation)
        Item found = items.get(0);
        System.out.println("First item in list: " + found.getItemName());
        System.out.println("Total items in inventory: " + items.size());

        // Pag-remove ng item
//        items.remove(1);   // tatanggalin ang keyboard (index 1)
//        System.out.println("After removing index 1, total items: " + items.size());
*/

    }
}