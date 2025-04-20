package com.nimdec.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class MyController {

    @GetMapping("/home")
    public CompletableFuture<String> home() {
        return CompletableFuture.supplyAsync(() -> "Home, Sweet Home");
    }

}
