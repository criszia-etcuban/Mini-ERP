public abstract class Order {
    protected String orderId;
    protected String orderDate;

    public Order(String orderId, String orderDate) {
        this.orderId = orderId;
        this.orderDate = orderDate;
    }

    public String getOrderId() { return orderId; }
    public String getOrderDate() { return orderDate; }

    // Abstract method: walang code, sa subclass ang gagawa
    public abstract double calculateTotal();

    // Ordinary method: shared na ng lahat ng subclass, hindi na kailangang ulitin
    public void printSummary() {
        System.out.println("Order ID: " + orderId + " | Date: " + orderDate
                + " | Total: ₱" + calculateTotal());
    }
}