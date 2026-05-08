package com.example.b;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void parseXmlReturnsRootElementName() throws Exception {
        assertEquals("root", new App().parseXml("<root><child/></root>"));
    }
}
