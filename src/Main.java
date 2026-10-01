public class Main {
    public static void main(String[] args) {
        // sample data
        Supplier supplier = new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com");
        // sample data
        Item mouse = new Item("A001", "Wireless Mouse", 100, 0.20, 100, 15, 100, 1, supplier);

        System.out.println("Item: " + mouse.getItemCode() + " - " + mouse.getItemName() + " (" + mouse.getCategoryName() + ")");
        System.out.println("Supplier: " + mouse.getSupplier().getSupplierName() + " | " + mouse.getSupplier().getContactNumber() + " | " + mouse.getSupplier().getEmail());
        System.out.println("Unit Price: " + mouse.getUnitPrice() + " | " + "Stock: " + mouse.getStock() + " pcs");
        System.out.println("Amount: ₱ " + mouse.priceWithTax());
        System.out.println("==============================");

        //loop method
        for (int day = 1; day <= 5; day++) {
            int order = 30;

            if (order > mouse.getStock()) {
                System.out.println("Day " + day + ": Not enough stock for order of " + order);
                break;
            }

            double total = mouse.calculateTotal(order);
            mouse.sell(order);

            System.out.println("Day " + day + ": sold " + order
                    + " | total ₱" + total
                    + " | stock " + mouse.getStock()
                    + " | " + mouse.getStockStatus());

            int reorderQty = mouse.calculateReorderQty();
            if (reorderQty > 0) {
                System.out.println("   -> ALERT!: Reorder " + reorderQty + " pcs from " + mouse.getSupplier().getSupplierName());
            }
        }
        System.out.println("==============================");
        System.out.println("Stock left: " + mouse.getStock() + " : " + mouse.getStockStatus());
        System.out.println("==============================");

        // New: PurchaseOrder at IApprovable
        Order po1 = new PurchaseOrder("PO-001", "2026-09-30", mouse, 50);
        po1.printSummary();

        if (po1 instanceof IApprovable approvable) {
            System.out.println("Status: " + approvable.getApprovalStatus());
            approvable.approve("Manager Cruz");
            System.out.println("Status: " + approvable.getApprovalStatus());
        }

        System.out.println("==============================");

        Order[] orders = {
                new PurchaseOrder("PO-002", "2026-10-01", mouse, 20),
                new PurchaseOrder("PO-003", "2026-10-02", mouse, 5)
        };

        System.out.println("Orders");
        for (Order order : orders) {
            order.printSummary();
        }
    }
}