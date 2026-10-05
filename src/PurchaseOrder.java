public class PurchaseOrder extends Order implements IApprovable {
    private Item item;
    private int qty;
    private boolean approved;
    private String approvedBy;

    public PurchaseOrder(String orderId, String orderDate, Item item, int qty) {
        super(orderId, orderDate);   // call the constructor of Order
        if (qty <= 0) {
            throw new InvalidPurchaseOrderException("Quantity must be greater than 0. Got: " + qty);
        }
        this.item = item;
        this.qty = qty;
        this.approved = false;
        this.approvedBy = null;
    }

    // need to implement, abstract in Order
    @Override
    public double calculateTotal() {
        return item.calculateTotal(qty);
    }

    // need to implement, in IApprovable File
    @Override
    public boolean isApproved() {
        return approved;
    }

    @Override
    public void approve(String approverName) {
        this.approved = true;
        this.approvedBy = approverName;
    }

    @Override
    public String getApprovalStatus() {
        if (approved) {
            return "APPROVED by " + approvedBy;
        }
        return "PENDING APPROVAL";
    }

    public Item getItem() { return item; }
    public int getQty() { return qty; }
}