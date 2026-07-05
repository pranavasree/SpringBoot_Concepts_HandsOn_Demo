package com.example.orderservice.service;

import com.example.orderservice.client.BookFeignClient;
import com.example.orderservice.dto.BookDto;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final BookFeignClient bookFeignClient;
    private final BookWebClientService bookWebClientService;

    public OrderService(
            BookFeignClient bookFeignClient,
            BookWebClientService bookWebClientService
    ) {
        this.bookFeignClient = bookFeignClient;
        this.bookWebClientService = bookWebClientService;
    }

    public String placeOrderUsingFeign(Long bookId) {
        BookDto book = bookFeignClient.getBookById(bookId);

        return "Order placed using OpenFeign for book: "
                + book.title()
                + " by "
                + book.author()
                + ". Price: $" + book.price();
    }

    public String placeOrderUsingWebClient(Long bookId) {
        BookDto book = bookWebClientService.getBookById(bookId);

        return "Order placed using WebClient for book: "
                + book.title()
                + " by "
                + book.author()
                + ". Price: $" + book.price();
    }
}