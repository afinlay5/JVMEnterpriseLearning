package com.nimdec.api.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
public class MyController {

    private final String trophy;

    public MyController(@Qualifier("realLife") String trophy) {
        this.trophy = trophy;
    }

    @GetMapping("/home")
    public CompletableFuture<String> home() {
        return supplyAsync(() -> trophy);
    }

}
