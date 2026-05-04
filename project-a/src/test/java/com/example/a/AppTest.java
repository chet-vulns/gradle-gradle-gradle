package com.example.a;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class AppTest {
    @Test
    public void greetingIsNotNull() {
        assertNotNull(new App().getGreeting());
    }
}
