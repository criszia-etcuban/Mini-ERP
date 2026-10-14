package com.example.minierp.model;

import com.example.minierp.exception.InvalidPurchaseOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchaseOrderTest {

    private Item mouse;

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com");
        mouse = new Item("A001", "Wireless Mouse", 100, 0.20, 50, 15, 100, 1, supplier);
    }

    @Test
    void totalComesFromItemPriceAndQuantity() {
        PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, 20);
        assertEquals(2400.0, po.calculateTotal(), 0.001);
    }

    @Test
    void newPurchaseOrderIsPending() {
        PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, 20);
        assertFalse(po.isApproved());
        assertEquals("PENDING APPROVAL", po.getApprovalStatus());
    }

    @Test
    void approvingChangesTheStatus() {
        PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, 20);
        po.approve("Manager Cruz");
        assertTrue(po.isApproved());
        assertEquals("APPROVED by Manager Cruz", po.getApprovalStatus());
    }

    @Test
    void zeroOrNegativeQuantityIsRejected() {
        assertThrows(InvalidPurchaseOrderException.class,
                () -> new PurchaseOrder("PO-002", "2026-10-02", mouse, 0));
        assertThrows(InvalidPurchaseOrderException.class,
                () -> new PurchaseOrder("PO-003", "2026-10-03", mouse, -5));
    }
}