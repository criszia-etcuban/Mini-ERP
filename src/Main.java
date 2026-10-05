import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        // ArrayList of Suppliers
        ArrayList<Supplier> suppliers = new ArrayList<>();
        suppliers.add(new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com"));
        suppliers.add(new Supplier("S002", "OfficeWorld Supply", "0917-888-2222", "officeworld@email.com"));

        // ArrayList of Items
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("A001", "Wireless Mouse", 100, 0.20, 100, 15, 100, 1, suppliers.get(0)));
        items.add(new Item("A002", "Mechanical Keyboard", 1500, 0.20, 40, 10, 50, 1, suppliers.get(0)));
        items.add(new Item("A003", "Office Chair", 3500, 0.20, 15, 5, 20, 2, suppliers.get(1)));

        Item item = items.get(0);

        System.out.println("Item: " + item.getItemCode() + " - " + item.getItemName() + " (" + item.getCategoryName() + ")");
        System.out.println("Supplier: " + item.getSupplier().getSupplierName() + " | " + item.getSupplier().getContactNumber() + " | " + item.getSupplier().getEmail());
        System.out.println("Unit Price: " + item.getUnitPrice() + " | " + "Stock: " + item.getStock() + " pcs");
        System.out.println("Amount: ₱ " + item.priceWithTax());
        System.out.println("==============================");

        //loop method
        for (int day = 1; day <= 5; day++) {
            int order = 30;

            if (order > item.getStock()) {
                System.out.println("Day " + day + ": Not enough stock for order of " + order);
                break;
            }

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
        purchaseOrders.add(new PurchaseOrder("PO-001", "2026-10-01", items.get(0), 20));
        purchaseOrders.add(new PurchaseOrder("PO-002", "2026-10-02", items.get(2), 5));

        System.out.println("=== PURCHASE ORDERS ===");
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

    }
}