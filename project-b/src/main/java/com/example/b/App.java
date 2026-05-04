package com.example.b;

import org.yaml.snakeyaml.Yaml;

public class App {
    public String getGreeting() {
        return "Hello from project-b!";
    }

    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        Object loaded = yaml.load("greeting: " + new App().getGreeting());
        System.out.println(loaded);
    }
}
