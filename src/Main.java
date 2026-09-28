public class Main {
    //switch method
    static String getCategoryName(int code) {
        switch (code) {
            case 1:
                return "Accessories";
            case 2:
                return "Hardware";
            case 3:
                return "Software";
            default:
                return "Unknown";
        }
    }
    static double calculateTotal(double unitPrice, double taxRate, int qty) {
        return unitPrice * (1 + taxRate) * qty;
    }
    //if-else method
    static String getStockStatus(int stock, int reorderLevel) {
        if (stock == 0) {
            return "OUT OF STOCK";
        } else if (stock <= reorderLevel) {
            return "LOW STOCK";
        } else {
            return "GOOD";
        }
    }
    static int calculateReorderQty(int stock, int reorderLevel, int targetStock) {
        if (stock > reorderLevel) {
            return 0;
        }
        return targetStock - stock;
    }
    public static void main(String[] args) {
        // Variables
        String itemCode = "A001";
        String itemName = "Wireless Mouse";
        double unitPrice = 100;
        double taxRate = .20;
        int stock = 100;
        int reorderLevel = 15;
        int targetStock = 100;
        int categoryCode = 1;               //choices(1,2,3)

        System.out.println("Item: " + itemCode + " - " + itemName + " (" + getCategoryName(categoryCode) + ")");
        System.out.println("Unit Price: " + unitPrice);

        // Arithmetic operators
        double addedTax = unitPrice * taxRate;
        double priceWithTax = unitPrice + addedTax;

        System.out.println("Amount: ₱ " + priceWithTax);
        System.out.println("Stock: " + stock + " pcs");
        System.out.println("==============================");

        //loop method
        for (int day = 1; day <= 5; day++) {
            int order = 30;

            if (order > stock) {
                System.out.println("Day " + day + ": Not enough stock for order of " + order);
                break;
            }

            stock -= order;
            double total = calculateTotal(unitPrice, taxRate, order);

            System.out.println("Day " + day + ": sold " + order
                    + " | total ₱" + total
                    + " | stock " + stock
                    + " | " + getStockStatus(stock, reorderLevel));

            int reorderQty = calculateReorderQty(stock, reorderLevel, targetStock);
            if (reorderQty > 0) {
                System.out.println("   -> ALERT!: Reorder " + reorderQty + " pcs");
            }
        }
        System.out.println("==============================");
        System.out.println("Stock left: " + stock);
    }
}