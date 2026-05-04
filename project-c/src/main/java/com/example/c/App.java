package com.example.c;

import com.google.common.base.Strings;

public class App {
    public String getGreeting() {
        return Strings.repeat("Hello from project-c! ", 1).trim();
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
    }
}
