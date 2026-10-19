package com.example.minierp.model;

import com.example.minierp.exception.InvalidPurchaseOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Purchase Order")
class PurchaseOrderTest {

    private Item mouse;

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com");
        mouse = new Item("A001", "Wireless Mouse", 100, 0.20, 50, 15, 100, 1, supplier);
    }

    @Test
    @DisplayName("Ang bagong PO ay pending")
    void newPurchaseOrderIsPending() {
        PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, 20);

        assertAll("bagong PO",
                () -> assertEquals("PO-001", po.getOrderId()),
                () -> assertEquals(20, po.getQty()),
                () -> assertSame(mouse, po.getItem()),
                () -> assertFalse(po.isApproved()),
                () -> assertEquals("PENDING APPROVAL", po.getApprovalStatus())
        );
    }

    @Test
    @DisplayName("Nagbabago ang status kapag na-approve")
    void approvingChangesTheStatus() {
        PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, 20);

        po.approve("Manager Cruz");

        assertAll("approved na PO",
                () -> assertTrue(po.isApproved()),
                () -> assertEquals("APPROVED by Manager Cruz", po.getApprovalStatus())
        );
    }

    @ParameterizedTest(name = "qty {0} -> total {1}")
    @CsvSource({
            "1,   120.0",
            "20,  2400.0",
            "100, 12000.0"
    })
    @DisplayName("Sumusunod ang total sa dami")
    void totalScalesWithQuantity(int qty, double expected) {
        PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, qty);
        assertEquals(expected, po.calculateTotal(), 0.001);
    }

    @ParameterizedTest(name = "qty {0} ay tinatanggihan")
    @ValueSource(ints = {0, -1, -5, -100})
    @DisplayName("Tinatanggihan ang qty na 0 o negatibo")
    void invalidQuantitiesAreRejected(int qty) {
        assertThrows(InvalidPurchaseOrderException.class,
                () -> new PurchaseOrder("PO-X", "2026-10-01", mouse, qty));
    }
}