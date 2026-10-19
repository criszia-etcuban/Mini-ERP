package com.example.minierp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerClassLifecycleDemoTest {

    private int counter = 0;
    private final String instanceId = Integer.toHexString(System.identityHashCode(this));

    @BeforeAll
    void beforeAll() {   // hindi static, pwede dahil PER_CLASS
        System.out.println("[per-class] BeforeAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("[per-class]   BeforeEach | instance " + instanceId + " | counter = " + counter);
    }

    @Test
    void firstTest() {
        counter++;
        System.out.println("[per-class]     firstTest | counter = " + counter);
    }

    @Test
    void secondTest() {
        counter++;
        System.out.println("[per-class]     secondTest | counter = " + counter);
    }

    @AfterEach
    void afterEach() {
        System.out.println("[per-class]   AfterEach");
    }

    @AfterAll
    void afterAll() {
        System.out.println("[per-class] AfterAll");
    }
}