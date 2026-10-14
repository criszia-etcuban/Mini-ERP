package com.example.minierp.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryTest {

    @Test
    void findReturnsTheValueWhenIdExists() {
        Repository<String> repo = new Repository<>();
        repo.add("K1", "hello");
        assertEquals("hello", repo.find("K1").get());
    }

    @Test
    void findReturnsEmptyWhenIdIsMissing() {
        Repository<String> repo = new Repository<>();
        assertTrue(repo.find("X").isEmpty());
    }

    @Test
    void countMatchesNumberOfEntries() {
        Repository<String> repo = new Repository<>();
        repo.add("K1", "a");
        repo.add("K2", "b");
        assertEquals(2, repo.count());
    }
}