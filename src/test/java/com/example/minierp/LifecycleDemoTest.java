package com.example.minierp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LifecycleDemoTest {

    private int counter = 0;
    private final String instanceId = Integer.toHexString(System.identityHashCode(this));

    @BeforeAll
    static void beforeAll() {
        System.out.println("[per-method] BeforeAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("[per-method]   BeforeEach | instance " + instanceId + " | counter = " + counter);
    }

    @Test
    void firstTest() {
        counter++;
        System.out.println("[per-method]     firstTest | counter = " + counter);
    }

    @Test
    void secondTest() {
        counter++;
        System.out.println("[per-method]     secondTest | counter = " + counter);
    }

    @AfterEach
    void afterEach() {
        System.out.println("[per-method]   AfterEach");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[per-method] AfterAll");
    }
}