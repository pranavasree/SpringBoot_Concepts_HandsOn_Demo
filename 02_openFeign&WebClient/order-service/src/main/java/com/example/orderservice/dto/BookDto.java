package com.example.orderservice.dto;

public record BookDto(
        Long id,
        String title,
        String author,
        double price
) {
}