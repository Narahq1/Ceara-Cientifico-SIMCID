package com.simcid.backend.dto;

import java.time.Instant;

/** Corpo padrao de erro devolvido pela API em respostas 4xx. */
public record ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path) {

    public static ErrorResponseDTO of(int status, String error, String message, String path) {
        return new ErrorResponseDTO(Instant.now(), status, error, message, path);
    }
}
