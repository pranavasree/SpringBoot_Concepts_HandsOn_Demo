package com.example.orderservice.service;

import com.example.orderservice.dto.BookDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BookWebClientService {

    private final WebClient bookWebClient;

    public BookWebClientService(WebClient bookWebClient) {
        this.bookWebClient = bookWebClient;
    }

    public BookDto getBookById(Long id) {
        return bookWebClient
                .get()
                .uri("/api/books/{id}", id)
                .retrieve()
                .bodyToMono(BookDto.class)
                .block();
    }
}