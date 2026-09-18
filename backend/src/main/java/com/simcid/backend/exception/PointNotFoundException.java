package com.simcid.backend.exception;

public class PointNotFoundException extends RuntimeException {
    public PointNotFoundException(String pointId) {
        super("Ponto de interesse nao encontrado: \"" + pointId + "\"");
    }
}
