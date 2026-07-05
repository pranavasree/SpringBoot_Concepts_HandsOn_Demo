package com.example.bookservice.controller;

import com.example.bookservice.dto.BookDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return new BookDto(
                id,
                "Clean Code",
                "Robert C. Martin",
                39.99
        );
    }
}