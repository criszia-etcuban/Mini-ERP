package com.example.minierp.model;

import com.example.minierp.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTest {

    private Item mouse;

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com");
        mouse = new Item("A001", "Wireless Mouse", 100, 0.20, 50, 15, 100, 1, supplier);
    }

    @Test
    void reorderQtyIsZeroWhenStockIsAboveReorderLevel() {
        assertEquals(0, mouse.calculateReorderQty());
    }

    @Test
    void reorderQtyFillsUpToTargetWhenStockIsLow() throws InsufficientStockException {
        mouse.sell(40);   // 50 - 40 = 10, mas mababa na sa reorder level na 15
        assertEquals(90, mouse.calculateReorderQty());   // 100 - 10
    }

    @Test
    void totalIncludesTax() {
        assertEquals(2400.0, mouse.calculateTotal(20), 0.001);   // 100 x 1.20 x 20
    }

    @Test
    void stockStatusChangesAfterSelling() throws InsufficientStockException {
        assertEquals("GOOD", mouse.getStockStatus());
        mouse.sell(40);
        assertEquals("LOW STOCK", mouse.getStockStatus());
    }

    @Test
    void sellingMoreThanStockThrowsException() {
        assertThrows(InsufficientStockException.class, () -> mouse.sell(100));
        assertEquals(50, mouse.getStock());   // hindi nabawasan
    }
}