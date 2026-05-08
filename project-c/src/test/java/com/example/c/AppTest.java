package com.example.c;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void resetTokenHasExpectedLength() {
        assertEquals(16, new App().generateResetToken().length());
    }
}
