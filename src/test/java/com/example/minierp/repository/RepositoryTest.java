package com.example.minierp.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repository")
class RepositoryTest {

    private Repository<String> repo;

    @BeforeEach
    void setUp() {
        repo = new Repository<>();
    }

    @Test
    @DisplayName("Ibinabalik ang mismong object na inilagay")
    void findReturnsTheSameObject() {
        String value = "hello";
        repo.add("K1", value);
        assertSame(value, repo.find("K1").get());
    }

    @Test
    @DisplayName("Empty ang resulta kapag walang ganoong ID")
    void findReturnsEmptyWhenIdIsMissing() {
        assertTrue(repo.find("X").isEmpty());
    }

    @Test
    @DisplayName("Mag-eerror ang get() sa empty na Optional")
    void getOnEmptyOptionalThrows() {
        assertThrows(NoSuchElementException.class, () -> repo.find("X").get());
    }

    @Test
    @DisplayName("Tama ang bilang ng laman")
    void countMatchesNumberOfEntries() {
        repo.add("K1", "a");
        repo.add("K2", "b");
        assertEquals(2, repo.count());
    }

    @Test
    @DisplayName("Kasama lahat sa getAll()")
    void getAllContainsEverything() {
        repo.add("K1", "a");
        repo.add("K2", "b");
        List<String> all = repo.getAll();
        assertAll(
                () -> assertEquals(2, all.size()),
                () -> assertTrue(all.containsAll(List.of("a", "b")))
        );
    }

    @Test
    @DisplayName("Napapalitan ang laman kapag pareho ang ID")
    void addingSameIdReplacesTheOldValue() {
        repo.add("K1", "old");
        repo.add("K1", "new");
        assertAll(
                () -> assertEquals(1, repo.count()),
                () -> assertEquals("new", repo.find("K1").get())
        );
    }
}