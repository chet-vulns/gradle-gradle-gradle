package com.example.a;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class AppTest {
    @Test
    public void hashIsNotNull() throws Exception {
        assertNotNull(new App(null).hashPassword("x"));
    }
}
