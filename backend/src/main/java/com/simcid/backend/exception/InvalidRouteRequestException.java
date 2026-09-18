package com.simcid.backend.exception;

public class InvalidRouteRequestException extends RuntimeException {
    public InvalidRouteRequestException(String message) {
        super(message);
    }
}
