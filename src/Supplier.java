public class Supplier {
    private String supplierId;
    private String supplierName;
    private String contactNumber;
    private String email;

    public Supplier(String supplierId, String supplierName, String contactNumber, String email) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}