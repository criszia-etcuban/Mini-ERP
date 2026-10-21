package com.example.minierp;

import com.example.minierp.exception.ItemNotFoundException;
import com.example.minierp.model.Item;
import com.example.minierp.model.Supplier;
import com.example.minierp.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Searching item by SKU")
class ItemLookupTest {

    private Repository<Item> itemRepo;
    private Item mouse;
    private Item keyboard;

    @BeforeEach
    void setUp() {
        Supplier supplier = new Supplier("S001", "TechParts Trading", "0917-123-4567", "techparts@email.com");
        mouse = new Item("A001", "Wireless Mouse", 100, 0.20, 50, 15, 100, 1, supplier);
        keyboard = new Item("A002", "Mechanical Keyboard", 1500, 0.20, 40, 10, 50, 1, supplier);

        itemRepo = new Repository<>();
        itemRepo.add("A001", mouse);
        itemRepo.add("A002", keyboard);
    }

    // ===================== KAPAG MAY NAHANAP =====================
    @Nested
    @DisplayName("When SKU exist")
    class WhenSkuExists {

        @Test
        @DisplayName("Existing SKU returns item")
        void existingSkuReturnsTheItem() throws ItemNotFoundException {
            Item result = Main.findItemBySku(itemRepo, "A001");
            assertSame(mouse, result);
        }

        @Test
        @DisplayName("Tamang item ang kinukuha kahit marami sa repo")
        void eachSkuReturnsItsOwnItem() throws ItemNotFoundException {
            assertAll(
                    () -> assertSame(mouse, Main.findItemBySku(itemRepo, "A001")),
                    () -> assertSame(keyboard, Main.findItemBySku(itemRepo, "A002"))
            );
        }
    }

    // ===================== KAPAG WALANG NAHANAP =====================
    @Nested
    @DisplayName("Kapag hindi mahanap ang SKU")
    class WhenSkuIsMissing {

        @Test
        @DisplayName("Nagt-throw ng ItemNotFoundException na may SKU sa mensahe")
        void missingSkuThrowsItemNotFoundException() {
            ItemNotFoundException e = assertThrows(
                    ItemNotFoundException.class,
                    () -> Main.findItemBySku(itemRepo, "A999"));

            assertEquals("No item found with SKU: A999", e.getMessage());
        }

        @ParameterizedTest(name = "SKU \"{0}\" ay hindi mahahanap")
        @NullAndEmptySource
        @ValueSource(strings = {"A999", "a001", " A001", "A001 ", "A00"})
        @DisplayName("Mga maling SKU")
        void invalidSkusAreNotFound(String sku) {
            assertThrows(ItemNotFoundException.class,
                    () -> Main.findItemBySku(itemRepo, sku));
        }

        @Test
        @DisplayName("Walang mahahanap sa walang laman na repo")
        void emptyRepositoryAlwaysThrows() {
            Repository<Item> empty = new Repository<>();
            assertThrows(ItemNotFoundException.class,
                    () -> Main.findItemBySku(empty, "A001"));
        }

        @Test
        @DisplayName("Hindi nagbabago ang repo pagkatapos ng bigong paghahanap")
        void failedLookupDoesNotChangeTheRepository() {
            assertThrows(ItemNotFoundException.class,
                    () -> Main.findItemBySku(itemRepo, "A999"));

            assertEquals(2, itemRepo.count());
        }
    }

    // ===================== ANG EXCEPTION MISMO =====================
    @Nested
    @DisplayName("Ang ItemNotFoundException mismo")
    class TheExceptionItself {

        @Test
        @DisplayName("Dala ang mensaheng ibinigay")
        void messageIsPassedThrough() {
            ItemNotFoundException e = new ItemNotFoundException("custom message");
            assertEquals("custom message", e.getMessage());
        }

        @Test
        @DisplayName("Checked exception ito, hindi RuntimeException")
        void isACheckedException() {
            assertAll(
                    () -> assertTrue(Exception.class.isAssignableFrom(ItemNotFoundException.class)),
                    () -> assertFalse(RuntimeException.class.isAssignableFrom(ItemNotFoundException.class))
            );
        }
    }
}