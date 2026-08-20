package com.example;

/**
 * Minimal "Hello World" application used to exercise the Build/Test/Deploy
 * stages of the sample Jenkins shared-library pipeline.
 */
public class App {

    public String getMessage() {
        return "Hello, World!";
    }

    public static void main(String[] args) {
        System.out.println(new App().getMessage());
    }
}
