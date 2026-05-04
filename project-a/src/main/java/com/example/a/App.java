package com.example.a;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public String getGreeting() {
        return "Hello from project-a!";
    }

    public static void main(String[] args) {
        App app = new App();
        LOGGER.info(app.getGreeting());
        System.out.println(app.getGreeting());
    }
}
