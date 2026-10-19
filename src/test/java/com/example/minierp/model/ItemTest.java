package com.example.minierp.model;

import com.example.minierp.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Item")
class ItemTest {

    private Item mouse;

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com");
        mouse = new Item("A001", "Wireless Mouse", 100, 0.20, 50, 15, 100, 1, supplier);
    }

    @Test
    @DisplayName("Tama ang detalye ng bagong item")
    void newItemHasCorrectDetails() {
        assertAll("mouse",
                () -> assertEquals("A001", mouse.getItemCode()),
                () -> assertEquals("Wireless Mouse", mouse.getItemName()),
                () -> assertEquals(50, mouse.getStock()),
                () -> assertEquals("Accessories", mouse.getCategoryName())
        );
    }

    @Test
    @DisplayName("Naka-link ang supplier sa item")
    void supplierIsLinkedToItem() {
        assertAll("supplier ng item", () -> {
            Supplier s = mouse.getSupplier();
            assertNotNull(s);   // kapag bagsak ito, hindi na tatakbo ang mga nasa ilalim
            assertAll("detalye ng supplier",
                    () -> assertEquals("S001", s.getSupplierId()),
                    () -> assertEquals("TechParts Trading", s.getSupplierName())
            );
        });
    }

    @Nested
    @DisplayName("Pagbebenta")
    class Selling {

        @Test
        @DisplayName("Nababawasan ang stock")
        void sellingReducesStock() throws InsufficientStockException {
            mouse.sell(20);
            assertEquals(30, mouse.getStock());
        }

        @Test
        @DisplayName("Pwedeng ibenta ang eksaktong natitirang stock")
        void sellingExactlyTheRemainingStockLeavesZero() throws InsufficientStockException {
            mouse.sell(50);
            assertAll(
                    () -> assertEquals(0, mouse.getStock()),
                    () -> assertEquals("OUT OF STOCK", mouse.getStockStatus())
            );
        }

        @Test
        @DisplayName("Mag-eerror kapag sobra ang ibebenta")
        void sellingMoreThanStockThrowsException() {
            InsufficientStockException e = assertThrows(
                    InsufficientStockException.class,
                    () -> mouse.sell(100));

            assertTrue(e.getMessage().contains("Not enough stock"));
            assertEquals(50, mouse.getStock(),
                    () -> "Hindi dapat nabawasan ang stock. Stock ngayon: " + mouse.getStock());
        }
    }

    @Nested
    @DisplayName("Presyo")
    class Pricing {

        @Test
        @DisplayName("Kasama ang tax sa total")
        void totalIncludesTax() {
            assertEquals(2400.0, mouse.calculateTotal(20), 0.001);   // 100 x 1.20 x 20
        }

        @Test
        @DisplayName("Hindi tinatanggap ang negatibong presyo")
        void negativePriceIsRejected() {
            mouse.setUnitPrice(-1);   // magpi-print ito ng "Invalid..." sa console, normal lang
            assertEquals(100.0, mouse.getUnitPrice(), 0.001);
        }
    }

    @Nested
    @DisplayName("Stock rules")
    class StockRules {

        @ParameterizedTest(name = "stock {0}, reorder level {1}, target {2} -> reorder {3}")
        @CsvSource({
                "50, 15, 100, 0",
                "16, 15, 100, 0",
                "15, 15, 100, 85",
                "10, 15, 100, 90",
                "0,  15, 100, 100"
        })
        @DisplayName("Dami ng ire-reorder")
        void reorderQtyDependsOnStock(int stock, int reorderLevel, int target, int expected) {
            Item item = new Item("A001", "Wireless Mouse", 100, 0.20,
                    stock, reorderLevel, target, 1, mouse.getSupplier());
            assertEquals(expected, item.calculateReorderQty());
        }

        @ParameterizedTest(name = "stock {0}, reorder level {1} -> {2}")
        @CsvSource({
                "0,  15, OUT OF STOCK",
                "1,  15, LOW STOCK",
                "15, 15, LOW STOCK",
                "16, 15, GOOD"
        })
        @DisplayName("Status ng stock")
        void stockStatusDependsOnStockAndReorderLevel(int stock, int reorderLevel, String expected) {
            Item item = new Item("A001", "Wireless Mouse", 100, 0.20,
                    stock, reorderLevel, 100, 1, mouse.getSupplier());
            assertEquals(expected, item.getStockStatus());
        }
    }

    @Disabled("Hinihintay pa ang patakaran: pwede ba ang presyong 0?")
    @Test
    @DisplayName("Presyong 0 (hindi pa napagdedesisyunan)")
    void zeroPriceRule() {
        fail("Hindi pa tapos ang test na ito");
    }
}