package com.example.minierp;

import com.example.minierp.exception.InsufficientStockException;
import com.example.minierp.exception.InvalidPurchaseOrderException;
import com.example.minierp.exception.ItemNotFoundException;
import com.example.minierp.model.IApprovable;
import com.example.minierp.model.Item;
import com.example.minierp.model.PurchaseOrder;
import com.example.minierp.model.Supplier;
import com.example.minierp.repository.Repository;

import java.util.LinkedList;
import  java.util.List;
import  java.util.TreeMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;
import  java.util.Optional;

public class Main {
    public static Item findItemBySku(Repository<Item> itemRepo, String sku) throws ItemNotFoundException {
            return itemRepo.find(sku)
                    .orElseThrow(() -> new ItemNotFoundException("No item with SKU: " + sku));
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

        List<Item> allItems = itemRepo.getAll();

        System.out.println("=== ALL SUPPLIERS ===");
        List<Supplier> allSuppliers = supplierRepo.getAll();
        for (Supplier s : allSuppliers) {
            System.out.println(s.getSupplierId() + " - " + s.getSupplierName() + " | " + s.getContactNumber() + " | " + s.getEmail());
        }
        System.out.println("Total suppliers: " + supplierRepo.count());
        System.out.println("==============================");

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
            itemList.forEach(i -> System.out.println("  - " + i.getItemCode() + " " + i.getItemName() + " | " + i.getCategoryName() + " | " + i.priceWithTax() + " | " + i.getStock() + " PCS"));
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

        System.out.println("==============================");
        // === 1. OPTIONAL BASICS ===
        System.out.println("=== 1. OPTIONAL BASICS ===");
        Optional<Item> found = itemRepo.find("A001");
        Optional<Item> missing = itemRepo.find("A999");

        System.out.println("A001 present? " + found.isPresent());
        System.out.println("A999 present? " + missing.isPresent());

        System.out.println("==============================");

        // === 2. ifPresent / ifPresentOrElse ===
        System.out.println("=== 2. ifPresent / ifPresentOrElse ===");
        itemRepo.find("A001")
                .ifPresent(x -> System.out.println("Found: " + x.getItemName()));

        itemRepo.find("A999").ifPresentOrElse(
                x -> System.out.println("Found: " + x.getItemName()),
                () -> System.out.println("Not found: A999"));

        System.out.println("==============================");

        // === 3. map + orElse ===
        System.out.println("=== 3. map + orElse ===");
        String name1 = itemRepo.find("A001")
                .map(Item::getItemName)
                .orElse("Unknown item");
        String name2 = itemRepo.find("A999")
                .map(Item::getItemName)
                .orElse("Unknown item");
        System.out.println("A001 -> " + name1);
        System.out.println("A999 -> " + name2);

        System.out.println("==============================");

        // === 4. filter sa Optional ===
        System.out.println("=== 4. filter sa Optional ===");
        boolean a001Low = itemRepo.find("A001")
                .filter(x -> x.getStock() <= x.getReorderLevel())
                .isPresent();
        boolean a002Low = itemRepo.find("A002")
                .filter(x -> x.getStock() <= x.getReorderLevel())
                .isPresent();
        System.out.println("A001 low stock? " + a001Low);
        System.out.println("A002 low stock? " + a002Low);

        System.out.println("==============================");

        // === 5. orElseThrow, gamit ang findItemBySku ===
        System.out.println("=== 5. orElseThrow ===");
        try {
            Item ok = findItemBySku(itemRepo, "A001");
            System.out.println("Found: " + ok.getItemName());

            Item bad = findItemBySku(itemRepo, "A999");
            System.out.println("Hindi na ito aabutan: " + bad.getItemName());
        } catch (ItemNotFoundException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        System.out.println("==============================");

// === 6. Optional mula sa Stream ===
        System.out.println("=== 6. Optional mula sa Stream ===");
        Optional<Item> mostExpensive = allItems.stream()
                .max(Comparator.comparingDouble(Item::getUnitPrice));
        mostExpensive.ifPresent(x ->
                System.out.println("Most expensive: " + x.getItemName() + " | ₱" + x.getUnitPrice()));

        Optional<Item> firstLowStock = allItems.stream()
                .filter(x -> x.getStock() <= x.getReorderLevel())
                .findFirst();
        System.out.println("First low-stock: "
                + firstLowStock.map(Item::getItemName).orElse("none"));

        Optional<PurchaseOrder> po2 = purchaseOrders.stream()
                .filter(po -> po.getOrderId().equals("PO-002"))
                .findFirst();
        po2.ifPresent(po -> System.out.println(po.getOrderId() + " | " + po.getApprovalStatus()));

        System.out.println("==============================");

// === 7. METHOD REFERENCES ===
        System.out.println("=== 7. METHOD REFERENCES ===");

// Uri 1 (method ng elemento) + Uri 3 (method ng tiyak na object)
        allItems.stream()
                .sorted(Comparator.comparing(Item::getItemName))
                .map(Item::getItemName)
                .forEach(System.out::println);

// Uri 2 (static method): Double::sum
        double totalPO = purchaseOrders.stream()
                .map(PurchaseOrder::calculateTotal)
                .reduce(0.0, Double::sum);
        System.out.println("Total PO value: ₱" + totalPO);

// Uri 4 (constructor): LinkedList::new
        LinkedList<String> nameList = allItems.stream()
                .map(Item::getItemName)
                .collect(Collectors.toCollection(LinkedList::new));
        System.out.println("Names: " + nameList);

// Hindi pwedeng gawing method reference, may dagdag na kondisyon, kaya lambda
        long lowCount = allItems.stream()
                .filter(x -> x.getStock() <= x.getReorderLevel())
                .count();
        System.out.println("Low-stock count: " + lowCount);
    }
}