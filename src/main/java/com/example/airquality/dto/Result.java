package com.example.airquality.dto;

import java.time.LocalDateTime;

public record Result<T>(int code, String message, T data, LocalDateTime timestamp) {
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data, LocalDateTime.now());
    }

    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message, null, LocalDateTime.now());
    }
}
