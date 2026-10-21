package com.example.minierp.model;

import com.example.minierp.exception.InvalidPurchaseOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Purchase Order")
class PurchaseOrderTest {

    private Item mouse;   // presyo 100
    private Item chair;   // presyo 3500

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com");
        mouse = new Item("A001", "Wireless Mouse", 100, 0.20, 50, 15, 100, 1, supplier);
        chair = new Item("A003", "Office Chair", 3500, 0.20, 15, 5, 20, 2, supplier);
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

    @Test
    @DisplayName("Hindi nagbabago ang total kapag na-approve")
    void approvalDoesNotChangeTheTotal() {
        PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, 20);
        double before = po.calculateTotal();

        po.approve("Manager Cruz");

        assertEquals(before, po.calculateTotal(), 0.001);
    }

    // ===================== PO TOTAL =====================
    @Nested
    @DisplayName("Total ng PO")
    class Total {

        @ParameterizedTest(name = "qty {0} -> total {1}")
        @CsvSource({
                "1,   120.0",
                "20,  2400.0",
                "100, 12000.0"
        })
        @DisplayName("Sumusunod ang total sa dami")
        void totalScalesWithQuantity(int qty, double expected) {
            PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, qty);
            assertEquals(expected, po.calculateTotal(), 0.001);   // 100 x 1.20 x qty
        }

        @Test
        @DisplayName("Magkaiba ang total ng magkaibang item")
        void differentItemsGiveDifferentTotals() {
            PurchaseOrder mousePo = new PurchaseOrder("PO-001", "2026-10-01", mouse, 5);
            PurchaseOrder chairPo = new PurchaseOrder("PO-002", "2026-10-01", chair, 5);

            assertAll(
                    () -> assertEquals(600.0, mousePo.calculateTotal(), 0.001),      // 100 x 1.20 x 5
                    () -> assertEquals(21000.0, chairPo.calculateTotal(), 0.001),    // 3500 x 1.20 x 5
                    () -> assertNotEquals(mousePo.calculateTotal(), chairPo.calculateTotal())
            );
        }

        @Test
        @DisplayName("Live ang total: sumusunod sa bagong presyo ng item")
        void totalFollowsLaterPriceChanges() {
            PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, 20);

            mouse.setUnitPrice(200);

            assertEquals(4800.0, po.calculateTotal(), 0.001);   // 200 x 1.20 x 20
        }
    }

    // ===================== PO AT DISCOUNT =====================
    @Nested
    @DisplayName("PO na may discount")
    class WithDiscount {

        @ParameterizedTest(name = "qty {0} -> total na may diskwento {1}")
        @CsvSource({
                "49,  5880.0",
                "50,  5400.0",
                "100, 10800.0"
        })
        @DisplayName("Total na may diskwento")
        void totalWithDiscount(int qty, double expected) {
            PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, qty);
            assertEquals(expected, po.calculateTotalWithDiscount(), 0.001);
        }

        @Test
        @DisplayName("Ang calculateTotal() ay walang diskwento pa rin")
        void plainTotalHasNoDiscount() {
            PurchaseOrder po = new PurchaseOrder("PO-001", "2026-10-01", mouse, 50);

            assertAll(
                    () -> assertEquals(6000.0, po.calculateTotal(), 0.001),               // 100 x 1.20 x 50
                    () -> assertEquals(5400.0, po.calculateTotalWithDiscount(), 0.001)    // (5000 - 500) x 1.20
            );
        }
    }

    // ===================== VALIDATION =====================
    @Nested
    @DisplayName("Validation ng PO")
    class Validation {

        @ParameterizedTest(name = "qty {0} ay tinatanggihan")
        @ValueSource(ints = {0, -1, -5, -100})
        @DisplayName("Tinatanggihan ang qty na 0 o negatibo")
        void invalidQuantitiesAreRejected(int qty) {
            assertThrows(InvalidPurchaseOrderException.class,
                    () -> new PurchaseOrder("PO-X", "2026-10-01", mouse, qty));
        }

        @Test
        @DisplayName("Hindi nababawasan ang stock ng item dahil sa PO")
        void creatingAPurchaseOrderDoesNotChangeStock() {
            new PurchaseOrder("PO-001", "2026-10-01", mouse, 20);
            assertEquals(50, mouse.getStock());
        }
    }
}