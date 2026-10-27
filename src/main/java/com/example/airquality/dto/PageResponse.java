package com.example.airquality.dto;

import java.util.List;

public record PageResponse<T>(List<T> content, long totalElements, int page, int size, int totalPages) {
    public static <T> PageResponse<T> of(List<T> content, long totalElements, int page, int size) {
        int totalPages = (int) Math.max(1, Math.ceil(totalElements / (double) size));
        return new PageResponse<>(content, totalElements, page, size, totalPages);
    }
}
