package com.example.b;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppTest {
    @Test
    void greetingIsNotNull() {
        assertNotNull(new App().getGreeting());
    }
}
