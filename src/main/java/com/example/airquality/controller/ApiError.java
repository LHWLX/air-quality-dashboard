package com.example.airquality.controller;

import java.time.LocalDateTime;

public record ApiError(LocalDateTime timestamp, int status, String message, String path) {
}

