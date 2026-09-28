public class Main {
    public static void main(String[] args) {
//        System.out.println("Hello world!");

        // Variables
        String itemCode = "A001";
        String itemName = "Wireless Mouse";
        double unitPrice = 100;
        double tax = .20;
        int stock = 100;
        int reorderLevel = 15;
        boolean isActive = true;
        int order = 9;

        // Arithmetic operators
        double addedTax = unitPrice * tax;
        double priceWithTax = unitPrice + addedTax;
        double totalAmount = priceWithTax * order;
        int stockNow = stock - order;

        // Relational operators
        boolean needsReorder = stockNow < reorderLevel;

        // Logical operators
        boolean canSell = isActive && stock > order;
        boolean canSellAgain = isActive && stockNow > order;
        boolean alert = needsReorder || stock == 0;

        // Output
        System.out.println("Item: " + itemCode + " - " + itemName);
        System.out.println("Unit Price: " + unitPrice);
        System.out.println("Amount: ₱ " + priceWithTax);
        System.out.println("Stock: " + stock + " pcs");
        System.out.println("==============================");
        System.out.println("order: " + order + " pcs");
        System.out.println("Total Amount: ₱ " + totalAmount);
        System.out.println("Can sell? " + canSell);
        System.out.println("==============================");
        System.out.println("Stock now: " + stockNow);
        System.out.println("Can sell again? " + canSellAgain);
        System.out.println("Needs reorder? " + needsReorder);
        System.out.println("Alert:" + alert);
    }
}