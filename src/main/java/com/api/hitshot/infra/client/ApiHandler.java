package com.api.hitshot.infra.client;


import java.util.function.Supplier;

public class ApiHandler {

    public static <T> T handle(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            throw new RuntimeException("api request failed", e);
        }
    }

}
