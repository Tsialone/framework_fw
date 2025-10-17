package controllers;

import annotations.Url;

public class UrlController {

    public UrlController () {}
    @Url(value = "/home")
    public void home() {
        System.out.println("hello home");
    }

    @Url( value = "/contact")
    public void contact() {
        System.out.println("hello contact");
    }
}
