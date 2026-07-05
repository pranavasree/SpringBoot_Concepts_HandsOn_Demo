package com.example.bookservice.dto;

public record BookDto(
        Long id,
        String title,
        String author,
        double price
) {
}