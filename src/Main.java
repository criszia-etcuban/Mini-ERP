import java.util.LinkedList;
import  java.util.List;
import  java.util.TreeMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;

public class Main {
    public static Item findItemBySku(Repository<Item> itemRepo, String sku) throws ItemNotFoundException {
        Item item = itemRepo.findById(sku);
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
        itemRepo.add("A001", new Item("A001", "Wireless Mouse", 100, 0.20, 10, 15, 100, 1, supplierRepo.findById("S001")));
        itemRepo.add("A002", new Item("A002", "Mechanical Keyboard", 1500, 0.20, 40, 10, 50, 1, supplierRepo.findById("S001")));
        itemRepo.add("A003", new Item("A003", "Office Chair", 3500, 0.20, 15, 5, 20, 2, supplierRepo.findById("S002")));

        // Purchase orders (valid quantities lahat)
        LinkedList<PurchaseOrder> purchaseOrders = new LinkedList<>();
        purchaseOrders.add(new PurchaseOrder("PO-001", "2026-10-01", itemRepo.findById("A001"), 20));
        purchaseOrders.add(new PurchaseOrder("PO-002", "2026-10-02", itemRepo.findById("A003"), 5));
        purchaseOrders.add(new PurchaseOrder("PO-003", "2026-10-03", itemRepo.findById("A002"), 10));

        purchaseOrders.get(0).approve("Manager Cruz");
        purchaseOrders.get(2).approve("Manager Cruz");

/*
        System.out.println("=== ALL SUPPLIERS (via getAll) ===");
        List<Supplier> allSuppliers = supplierRepo.getAll();
        for (Supplier s : allSuppliers) {
            System.out.println(s.getSupplierId() + " - " + s.getSupplierName() + " | " + s.getContactNumber() + " | " + s.getEmail());
        }
        System.out.println("Total suppliers: " + supplierRepo.count());
        System.out.println("==============================");
*/

/*
        System.out.println("=== ALL ITEMS (via getAll) // ITEMS SORTED BY SKU ===");
        // === TreeMap: parehong Map, pero naka-sort ayon sa key (SKU code) ===
        TreeMap<String, Item> sortedByCode = new TreeMap<>();
        for (Item i : itemRepo.getAll()) {
            sortedByCode.put(i.getItemCode(), i);
        }

        for (Map.Entry<String, Item> entry : sortedByCode.entrySet()) {
            Item i = entry.getValue();
            System.out.println(i.getItemCode() + " - " + i.getItemName()
                    + " | Stock: " + i.getStock()
                    + " | Category: " + i.getCategoryName()
                    + " | Price: " + i.priceWithTax());
        }
        System.out.println("Total items: " + itemRepo.count());
        System.out.println("==============================");
*/

/*
        // === Predicate: yes/no check, nasa anyong lambda ===
        Predicate<Item> isLowStock = item -> item.getStock() <= item.getReorderLevel();
        System.out.println("=== LOW STOCK ITEMS ===");
        for (Item item : itemRepo.getAll()) {
            if (isLowStock.test(item)) {
                System.out.println(item.getItemCode() + " - " + item.getItemName() + " (stock: " + item.getStock() + ")");
            }
        }
        System.out.println("--------------------------------");
        System.out.println("=== EXPENSIVE ===");
        Predicate<Item> isExpensive = item -> item.priceWithTax() > 1000;
        for (Item item : itemRepo.getAll()) {
            if (isExpensive.test(item)) {
                System.out.println(item.getItemName() + "Price: " + item.priceWithTax());
            }
        }
        System.out.println("==============================");
*/

/*
        // === Consumer: gumawa ng bagay sa bawat item, nasa anyong lambda ===
        Consumer<Item> printSummary = item ->
                System.out.println(item.getItemCode() + " | " + item.getItemName() + " | ₱" + item.priceWithTax());

        System.out.println("=== ALL ITEMS (via Consumer) ===");
        itemRepo.getAll().forEach(printSummary);

        System.out.println("==============================");
*/

/*
        // === Function: i-transform ang Item papunta sa iba, nasa anyong lambda ===
        Function<Item, String> toLabel = item -> item.getItemName() + " (" + item.getCategoryName() + ")";

        System.out.println("=== LABELS (via Function) ===");
        for (Item item : itemRepo.getAll()) {
            System.out.println(toLabel.apply(item));
        }
        System.out.println("--------------------------------");
        Function<Item, Double> getTotalValue = item -> item.getUnitPrice() * item.getStock();
        for (Item item : itemRepo.getAll()) {
            System.out.println(item.getItemName() + " - Total Value of all stock: " + getTotalValue.apply(item));
        }
        System.out.println("==============================");
*/

/*
        System.out.println("=== Reorder Qty ===");
        Item item = itemRepo.findById("A001");

        try {
            //loop method
            for (int day = 1; day <= 5; day++) {
                int order = 30;

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
*/

/*
        // LinkedList of PurchaseOrders, same List interface, magkaiba lang sa loob
        LinkedList<PurchaseOrder> purchaseOrders = new LinkedList<>();
        System.out.println("=== PURCHASE ORDERS ===");
        try {
            purchaseOrders.add(new PurchaseOrder("PO-001", "2026-10-01", itemRepo.findById("A001"), 20));
            purchaseOrders.add(new PurchaseOrder("PO-002", "2026-10-02", itemRepo.findById("A003"),  5));
            //sample data for not found
            Item item3 = findItemBySku(itemRepo,"A999");
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
        System.out.println("==============================");
*/

/*
        System.out.println("=== LOOK UP ===");
        Item found = itemRepo.findById("A001");
        System.out.println("Found: " + found.getItemName());

        Supplier foundSupplier = supplierRepo.findById("S001");
        System.out.println("Found: " + foundSupplier.getSupplierName());

        Item notFound = itemRepo.findById("A999");
        if (notFound == null) {
            System.out.println("A999 not found in inventory.");
        }
        System.out.println("==============================");
*/

        List<Item> allItems = itemRepo.getAll();

/*
        // === 1. FILTER: low-stock items ===
        System.out.println("=== LOW STOCK ITEMS (filter) ===");
        List<Item> lowStockItems = allItems.stream()
                .filter(item -> item.getStock() <= item.getReorderLevel())
                .toList();

        lowStockItems.forEach(item ->
                System.out.println(item.getItemCode() + " - " + item.getItemName()
                        + " | Stock: " + item.getStock()
                        + " | Reorder level: " + item.getReorderLevel()));

        System.out.println("==============================");
*/

/*
        // === 2. MAP: kunin lang ang pangalan ng bawat item ===
        System.out.println("=== ITEM NAMES (map) ===");
        List<String> itemNames = allItems.stream()
                .map(item -> item.getItemName())
                .toList();
        System.out.println(itemNames);

        System.out.println("==============================");

        // === 3. FILTER + MAP: pangalan ng low-stock items lang ===
        System.out.println("=== LOW STOCK NAMES (filter + map) ===");
        String lowStockNames = allItems.stream()
                .filter(item -> item.getStock() <= item.getReorderLevel())
                .map(item -> item.getItemName())
                .collect(Collectors.joining(", "));
        System.out.println(lowStockNames);

        System.out.println("==============================");
*/

/*
        // === 4. COUNT ===
        long lowStockCount = allItems.stream()
                .filter(item -> item.getStock() <= item.getReorderLevel())
                .count();
        System.out.println("Number of low-stock items: " + lowStockCount);

        System.out.println("==============================");
*/

/*
        // === 5. MAP + SUM: total PO value ===
        double totalPOValue = purchaseOrders.stream()
                .mapToDouble(po -> po.calculateTotal())
                .sum();
        System.out.println("Total PO value: ₱" + totalPOValue);

        // === 6. REDUCE: parehong resulta, pero explicit ang pagsasama ===
        double totalViaReduce = purchaseOrders.stream()
                .map(po -> po.calculateTotal())
                .reduce(0.0, (a, b) -> a + b);
        System.out.println("Total PO value (reduce): ₱" + totalViaReduce);

        // === 7. FILTER + SUM: total ng APPROVED na PO lang ===
        double approvedTotal = purchaseOrders.stream()
                .filter(po -> po.isApproved())
                .mapToDouble(po -> po.calculateTotal())
                .sum();
        System.out.println("Total APPROVED PO value: ₱" + approvedTotal);

        System.out.println("==============================");
*/

/*
        // === 8. MAP + SUM: total value ng inventory (price x stock) ===
        double inventoryValue = allItems.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getStock())
                .sum();
        System.out.println("Total inventory value: ₱" + inventoryValue);
*/
        // === 1. GROUPING: items bawat supplier ===
        System.out.println("=== ITEMS BY SUPPLIER ===");
        Map<String, List<Item>> itemsBySupplier = allItems.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getSupplier().getSupplierName(),   // paano mag-grupo
                        TreeMap::new,                              // naka-sort ang supplier names
                        Collectors.toList()));                     // ano ang laman ng bawat grupo

        itemsBySupplier.forEach((supplierName, itemList) -> {
            System.out.println(supplierName);
            itemList.forEach(i -> System.out.println("  - " + i.getItemCode() + " " + i.getItemName()));
        });
        System.out.println("==============================");

        // === 2. GROUPING + COUNTING: ilang item bawat supplier ===
        System.out.println("=== ITEM COUNT BY SUPPLIER ===");
        Map<String, Long> countBySupplier = allItems.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getSupplier().getSupplierName(),
                        TreeMap::new,
                        Collectors.counting()));

        countBySupplier.forEach((name, count) ->
                System.out.println(name + " -> " + count + " item(s)"));
        System.out.println("==============================");

        // === 3. GROUPING + SUMMING: halaga ng stock bawat supplier ===
        System.out.println("=== STOCK VALUE BY SUPPLIER ===");
        Map<String, Double> valueBySupplier = allItems.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getSupplier().getSupplierName(),
                        TreeMap::new,
                        Collectors.summingDouble(i -> i.getUnitPrice() * i.getStock())));

        valueBySupplier.forEach((name, value) ->
                System.out.println(name + " -> ₱" + value));

        System.out.println("==============================");

// === 4. GROUPING + MAPPING: pangalan lang ng items bawat supplier ===
        System.out.println("=== ITEM NAMES BY SUPPLIER ===");
        Map<String, List<String>> namesBySupplier = allItems.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getSupplier().getSupplierName(),
                        TreeMap::new,
                        Collectors.mapping(Item::getItemName, Collectors.toList())));

        System.out.println(namesBySupplier);

        System.out.println("==============================");

// === 5. FILTER + GROUPING: ilang piraso ang dapat i-reorder, bawat supplier ===
        System.out.println("=== REORDER NEEDED BY SUPPLIER ===");
        Map<String, Integer> reorderBySupplier = allItems.stream()
                .filter(i -> i.getStock() <= i.getReorderLevel())
                .collect(Collectors.groupingBy(
                        i -> i.getSupplier().getSupplierName(),
                        TreeMap::new,
                        Collectors.summingInt(Item::calculateReorderQty)));

        reorderBySupplier.forEach((name, qty) ->
                System.out.println("Contact " + name + " -> reorder " + qty + " pcs"));

        System.out.println("==============================");

// === 6. SORTING ===
        System.out.println("=== SORTED BY PRICE (mura -> mahal) ===");
        allItems.stream()
                .sorted(Comparator.comparingDouble(Item::getUnitPrice))
                .forEach(i -> System.out.println(i.getItemName() + " | ₱" + i.getUnitPrice()));

        System.out.println("=== SORTED BY PRICE (mahal -> mura) ===");
        allItems.stream()
                .sorted(Comparator.comparingDouble(Item::getUnitPrice).reversed())
                .forEach(i -> System.out.println(i.getItemName() + " | ₱" + i.getUnitPrice()));

        System.out.println("=== SORTED BY NAME (A -> Z) ===");
        allItems.stream()
                .sorted(Comparator.comparing(Item::getItemName))
                .forEach(i -> System.out.println(i.getItemName()));

        System.out.println("=== SORTED BY CATEGORY, THEN PRICE ===");
        allItems.stream()
                .sorted(Comparator.comparingInt(Item::getCategoryCode)
                        .thenComparingDouble(Item::getUnitPrice))
                .forEach(i -> System.out.println(i.getCategoryName() + " | " + i.getItemName() + " | ₱" + i.getUnitPrice()));

        System.out.println("==============================");

// === 7. PURCHASE ORDERS: grouping at sorting ===
        System.out.println("=== PO TOTAL BY STATUS ===");
        Map<String, Double> poByStatus = purchaseOrders.stream()
                .collect(Collectors.groupingBy(
                        po -> po.isApproved() ? "APPROVED" : "PENDING",
                        TreeMap::new,
                        Collectors.summingDouble(PurchaseOrder::calculateTotal)));

        poByStatus.forEach((status, total) ->
                System.out.println(status + " -> ₱" + total));

        System.out.println("=== PO TOTAL BY SUPPLIER ===");
        Map<String, Double> poBySupplier = purchaseOrders.stream()
                .collect(Collectors.groupingBy(
                        po -> po.getItem().getSupplier().getSupplierName(),
                        TreeMap::new,
                        Collectors.summingDouble(PurchaseOrder::calculateTotal)));

        poBySupplier.forEach((name, total) ->
                System.out.println(name + " -> ₱" + total));

        System.out.println("=== PO RANKED BY TOTAL (pinakamalaki muna) ===");
        purchaseOrders.stream()
                .sorted(Comparator.comparingDouble(PurchaseOrder::calculateTotal).reversed())
                .forEach(po -> System.out.println(po.getOrderId() + " | ₱" + po.calculateTotal()
                        + " | " + po.getApprovalStatus()));

    }
}