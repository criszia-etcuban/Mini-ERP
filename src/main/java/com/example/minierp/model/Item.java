package com.example.minierp.model;

import com.example.minierp.exception.InsufficientStockException;
import java.util.HashMap;

public class Item {
    // Encapsulation
    private String itemCode;
    private String itemName;
    private double unitPrice;
    private double taxRate;
    private int stock;
    private int reorderLevel;
    private int targetStock;
    private int categoryCode;
    private Supplier supplier;   // Another class

    // Constructor
    public Item(String itemCode, String itemName, double unitPrice, double taxRate,
                int stock, int reorderLevel, int targetStock, int categoryCode, Supplier supplier) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
        this.stock = stock;
        this.reorderLevel = reorderLevel;
        this.targetStock = targetStock;
        this.categoryCode = categoryCode;
        this.supplier = supplier;
    }

    // Getters
    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public double getUnitPrice() { return unitPrice; }
//    public double getTaxRate() { return taxRate; }
    public int getStock() { return stock; }
    public int getReorderLevel() { return reorderLevel; }
//    public int getTargetStock() { return targetStock; }
    public int getCategoryCode() { return categoryCode; }
    public Supplier getSupplier() { return supplier; }

    // Setters
    public void setStock(int stock) {
        if (stock < 0) {
            System.out.println("Out of Stock");
            return;
        }
        this.stock = stock;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) {
            System.out.println("Invalid Price");
            return;
        }
        this.unitPrice = unitPrice;
    }

    // old static method
    public String getCategoryName() {
        switch (categoryCode) {
            case 1: return "Accessories";
            case 2: return "Hardware";
            case 3: return "Software";
            default: return "Unknown";
        }
    }

    public double calculateTotal(int qty) {
        return unitPrice * (1 + taxRate) * qty;
    }

//    public double priceWithTax() { return  unitPrice * (1 + taxRate);}

    public String getStockStatus() {
        if (stock == 0) {
            return "OUT OF STOCK";
        } else if (stock <= reorderLevel) {
            return "LOW STOCK";
        } else {
            return "GOOD";
        }
    }

    public int calculateReorderQty() {
        if (stock > reorderLevel) {
            return 0;
        }
        return targetStock - stock;
    }

    public void sell(int qty) throws InsufficientStockException {
        if (qty > stock) {
            throw new InsufficientStockException(
                    "Not enough stock for " + itemName + " requested: " + qty + ", available: " + stock
            );
        }
        stock -= qty;
    }

    public static final int DISCOUNT_MIN_QTY = 50;
    public static final double DISCOUNT_RATE = 0.10;

    // Amount of discount (before tax). No discount if not reach 50 pcs.
    public double calculateDiscount(int qty) {
        if (qty < DISCOUNT_MIN_QTY) {
            return 0.0;
        }
        return unitPrice * qty * DISCOUNT_RATE;
    }

    // Total with discount: bawas muna ang diskwento, saka dagdagan ng tax.
    public double calculateTotalWithDiscount(int qty) {
        double subtotal = unitPrice * qty;
        double afterDiscount = subtotal - calculateDiscount(qty);
        return afterDiscount * (1 + taxRate);
    }
}