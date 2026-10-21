package com.example.minierp;

import com.example.minierp.exception.ItemNotFoundException;
import com.example.minierp.exception.InsufficientStockException;
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
                    .orElseThrow(() -> new ItemNotFoundException("No item found with SKU: " + sku));
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
        System.out.println("=============================================================");

        System.out.println("=== ALL ITEMS ===");
        // GROUPING: items bawat supplier ===
        System.out.println("=== ITEMS BY SUPPLIER ===");
        Map<String, List<Item>> itemsBySupplier = allItems.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getSupplier().getSupplierName(),   // paano mag-grupo
                        TreeMap::new,                              // naka-sort ang supplier names
                        Collectors.toList()));                     // ano ang laman ng bawat grupo

        itemsBySupplier.forEach((supplierName, itemList) -> {
            System.out.println(supplierName);
            itemList.forEach(i -> System.out.println("  - " + i.getItemCode() + " " + i.getItemName() + " | " + i.getCategoryName() + " | " + i.getUnitPrice() + " | " + i.getStock() + " PCS"));
        });
        System.out.println("----------------------------------------------");

        // GROUPING + COUNTING: ilang item bawat supplier ===
        System.out.println("=== ITEM COUNT BY SUPPLIER ===");
        Map<String, Long> countBySupplier = allItems.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getSupplier().getSupplierName(),
                        TreeMap::new,
                        Collectors.counting()));

        countBySupplier.forEach((name, count) ->
                System.out.println(name + " -> " + count + " item(s)"));
        System.out.println("----------------------------------------------");

        // === Function: i-transform ang Item papunta sa iba, nasa anyong lambda ===
        Function<Item, String> toLabel = item -> item.getItemName() + " (" + item.getCategoryName() + ")";

        System.out.println("=== LABELS (via Function) ===");
        for (Item item : itemRepo.getAll()) {
            System.out.println(toLabel.apply(item));
        }
        System.out.println("----------------------------------------------");

        Function<Item, Double> getTotalValue = item -> item.getUnitPrice() * item.getStock();
        for (Item item : itemRepo.getAll()) {
            System.out.println(item.getItemName() + " - Total Value of all stock: " + getTotalValue.apply(item));
        }
        System.out.println("----------------------------------------------");

        System.out.println("=== LOOK UP ===");
        Item find = itemRepo.findById("A001");
        System.out.println("Found: " + find.getItemName());

        Supplier findSupplier = supplierRepo.findById("S001");
        System.out.println("Found: " + findSupplier.getSupplierName());

        Item notFound = itemRepo.findById("A999");
        if (notFound == null) {
            System.out.println("A999 not found in inventory.");
        }
        System.out.println("=============================================================");

        // === SORTING ===
        System.out.println("=== SORTED BY PRICE (mura -> mahal) ===");
        allItems.stream()
                .sorted(Comparator.comparingDouble(Item::getUnitPrice))
                .forEach(i -> System.out.println(i.getItemName() + " | ₱" + i.getUnitPrice()));
        System.out.println("----------------------------------------------");

        System.out.println("=== SORTED BY PRICE (mahal -> mura) ===");
        allItems.stream()
                .sorted(Comparator.comparingDouble(Item::getUnitPrice).reversed())
                .forEach(i -> System.out.println(i.getItemName() + " | ₱" + i.getUnitPrice()));
        System.out.println("----------------------------------------------");

        System.out.println("=== SORTED BY NAME (A -> Z) ===");
        allItems.stream()
                .sorted(Comparator.comparing(Item::getItemName))
                .forEach(i -> System.out.println(i.getItemName()));
        System.out.println("----------------------------------------------");

        System.out.println("=== SORTED BY CATEGORY, THEN PRICE ===");
        allItems.stream()
                .sorted(Comparator.comparingInt(Item::getCategoryCode)
                        .thenComparingDouble(Item::getUnitPrice))
                .forEach(i -> System.out.println(i.getCategoryName() + " | " + i.getItemName() + " | ₱" + i.getUnitPrice()));

        System.out.println("=============================================================");

        // === PURCHASE ORDERS: grouping at sorting ===
        System.out.println("=== PURCHASE ORDER ===");
        System.out.println("=== PO TOTAL BY STATUS ===");
        Map<String, Double> poByStatus = purchaseOrders.stream()
                .collect(Collectors.groupingBy(
                        po -> po.isApproved() ? "APPROVED" : "PENDING",
                        TreeMap::new,
                        Collectors.summingDouble(PurchaseOrder::calculateTotal)));

        poByStatus.forEach((status, total) ->
                System.out.println(status + " -> ₱" + total));
        System.out.println("----------------------------------------------");

        System.out.println("=== PO TOTAL BY SUPPLIER ===");
        Map<String, Double> poBySupplier = purchaseOrders.stream()
                .collect(Collectors.groupingBy(
                        po -> po.getItem().getSupplier().getSupplierName(),
                        TreeMap::new,
                        Collectors.summingDouble(PurchaseOrder::calculateTotal)));

        poBySupplier.forEach((name, total) ->
                System.out.println(name + " -> ₱" + total));
        System.out.println("----------------------------------------------");

        System.out.println("=== PO RANKED BY TOTAL (pinakamalaki muna) ===");
        purchaseOrders.stream()
                .sorted(Comparator.comparingDouble(PurchaseOrder::calculateTotal).reversed())
                .forEach(po -> System.out.println(po.getOrderId() + " | ₱" + po.calculateTotal()
                        + " | " + po.getApprovalStatus()));

        System.out.println("=============================================================");
        // === 1. OPTIONAL BASICS ===
        System.out.println("=== 1. OPTIONAL BASICS ===");
        Optional<Item> found = itemRepo.find("A001");
        Optional<Item> missing = itemRepo.find("A999");

        System.out.println("A001 present? " + found.isPresent());
        System.out.println("A999 present? " + missing.isPresent());

        System.out.println("----------------------------------------------");

        // === 2. ifPresent / ifPresentOrElse ===
        System.out.println("=== 2. ifPresent / ifPresentOrElse ===");
        itemRepo.find("A001")
                .ifPresent(x -> System.out.println("Found: " + x.getItemName()));

        itemRepo.find("A999").ifPresentOrElse(
                x -> System.out.println("Found: " + x.getItemName()),
                () -> System.out.println("Not found: A999"));

        System.out.println("----------------------------------------------");

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

        System.out.println("----------------------------------------------");

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

        System.out.println("----------------------------------------------");

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

        System.out.println("=============================================================");

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

        System.out.println("=============================================================");

    }
}