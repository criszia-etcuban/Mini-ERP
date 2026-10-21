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

    private Item mouse;   // presyo 100, tax 20%, stock 50, reorder level 15, target 100

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
                () -> assertEquals(15, mouse.getReorderLevel()),
                () -> assertEquals("Accessories", mouse.getCategoryName())
        );
    }

    @ParameterizedTest(name = "category {0} -> {1}")
    @CsvSource({
            "1,  Accessories",
            "2,  Hardware",
            "3,  Software",
            "99, Unknown"
    })
    @DisplayName("Name of category")
    void categoryNameDependsOnCode(int code, String expected) {
        Item item = new Item("A001", "Test Item", 100, 0.20,
                50, 15, 100, code, mouse.getSupplier());
        assertAll(
                () -> assertEquals(code, item.getCategoryCode()),
                () -> assertEquals(expected, item.getCategoryName())
        );
    }

    @Test
    @DisplayName("Naka-link ang supplier sa item")
    void supplierIsLinkedToItem() {
        assertAll("supplier ng item", () -> {
            Supplier s = mouse.getSupplier();
            assertNotNull(s);
            assertAll("detalye ng supplier",
                    () -> assertEquals("S001", s.getSupplierId()),
                    () -> assertEquals("TechParts Trading", s.getSupplierName()),
                    () -> assertEquals("0917-123-4567", s.getContactNumber()),
                    () -> assertEquals("techparts@email.com", s.getEmail())
            );
        });
    }

    // ===================== Selling =====================
    @Nested
    @DisplayName("Sell")
    class Selling {

        @Test
        @DisplayName("Stock Reduced")
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
            assertEquals(50, mouse.getStock());
        }
    }

    // ===================== STOCK VALIDATION =====================
    @Nested
    @DisplayName("Stock validation")
    class StockValidation {

        @Test
        @DisplayName("Tinatanggap ang positibong stock")
        void setStockAcceptsPositiveNumber() {
            mouse.setStock(30);
            assertEquals(30, mouse.getStock());
        }

        @Test
        @DisplayName("Tinatanggap ang stock na 0 at nagiging OUT OF STOCK")
        void setStockAcceptsZero() {
            mouse.setStock(0);
            assertAll(
                    () -> assertEquals(0, mouse.getStock()),
                    () -> assertEquals("OUT OF STOCK", mouse.getStockStatus())
            );
        }

        @Test
        @DisplayName("Tinatanggihan ang negatibong stock at hindi nagbabago ang dati")
        void setStockRejectsNegativeNumber() {
            mouse.setStock(-1);   // magpi-print ng "Invalid..." sa console, normal lang
            assertEquals(50, mouse.getStock());
        }

        @Test
        @DisplayName("Isa lang ang lampas sa stock, tinatanggihan na")
        void sellingOneMoreThanStockIsRejected() {
            assertThrows(InsufficientStockException.class, () -> mouse.sell(51));
            assertEquals(50, mouse.getStock());
        }

        @Test
        @DisplayName("May detalye ang mensahe ng error")
        void errorMessageHasItemNameAndNumbers() {
            InsufficientStockException e = assertThrows(
                    InsufficientStockException.class,
                    () -> mouse.sell(51));

            assertAll("message",
                    () -> assertTrue(e.getMessage().contains("Wireless Mouse")),
                    () -> assertTrue(e.getMessage().contains("51")),
                    () -> assertTrue(e.getMessage().contains("50"))
            );
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
    }

    // ===================== PRICE AND TAX =====================
    @Nested
    @DisplayName("Price and tax")
    class Pricing {

        @Test
        @DisplayName("Total includes tax")
        void totalIncludesTax() {
            assertEquals(2400.0, mouse.calculateTotal(20), 0.001);   // 100 x 1.20 x 20
        }

        @Test
        @DisplayName("Nagbabago ang total kapag binago ang presyo")
        void changingPriceChangesTotal() {
            mouse.setUnitPrice(150);
            assertEquals(1800.0, mouse.calculateTotal(10), 0.001);   // 150 x 1.20 x 10
        }

        @Test
        @DisplayName("Hindi tinatanggap ang negatibong presyo")
        void negativePriceIsRejected() {
            mouse.setUnitPrice(-1);
            assertEquals(100.0, mouse.getUnitPrice(), 0.001);
        }
    }

    // ===================== DISCOUNT =====================
    @Nested
    @DisplayName("Discount")
    class Discount {

        @ParameterizedTest(name = "qty {0} -> Discount {1}")
        @CsvSource({
                "1,   0.0",
                "10,  0.0",
                "49,  0.0",
                "50,  500.0",
                "100, 1000.0"
        })
        @DisplayName("Discounts starts at 50 pcs")
        void discountStartsAtFiftyUnits(int qty, double expected) {
            assertEquals(expected, mouse.calculateDiscount(qty), 0.001);   // 100 x qty x 10%
        }

        @ParameterizedTest(name = "qty {0} -> total {1}")
        @CsvSource({
                "10,  1200.0",
                "49,  5880.0",
                "50,  5400.0",
                "100, 10800.0"
        })
        @DisplayName("Discount first, then tax")
        void totalWithDiscountAppliesTaxAfterDiscount(int qty, double expected) {
            // 50 pcs: (5000 - 500) x 1.20 = 5400
            assertEquals(expected, mouse.calculateTotalWithDiscount(qty), 0.001);
        }

        @Test
        @DisplayName("Discount follows Unit Price")
        void discountFollowsUnitPrice() {
            mouse.setUnitPrice(1500);
            assertEquals(7500.0, mouse.calculateDiscount(50), 0.001);   // 1500 x 50 x 10%
        }

        @Test
        @DisplayName("NO discount in 0 and negative qty")
        void zeroOrNegativeQuantityHasNoDiscount() {
            assertAll(
                    () -> assertEquals(0.0, mouse.calculateDiscount(0), 0.001),
                    () -> assertEquals(0.0, mouse.calculateDiscount(-10), 0.001)
            );
        }

        @Test
        @DisplayName("Hindi ginagalaw ng pagkuwenta ang stock")
        void calculatingDiscountDoesNotTouchStock() {
            mouse.calculateTotalWithDiscount(50);
            assertEquals(50, mouse.getStock());
        }

        @Test
        @DisplayName("Mas mura ang 50 pcs kaysa 49 pcs (epekto ng threshold)")
        void fiftyUnitsCostLessThanFortyNineUnits() {
            assertTrue(mouse.calculateTotalWithDiscount(50) < mouse.calculateTotalWithDiscount(49));
        }
    }

    @Disabled("Hinihintay pa ang patakaran: pwede ba ang presyong 0?")
    @Test
    @DisplayName("Presyong 0 (hindi pa napagdedesisyunan)")
    void zeroPriceRule() {
        fail("Hindi pa tapos ang test na ito");
    }
}