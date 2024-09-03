package com.luv2code.demo.controller;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.entity.Book;
import com.luv2code.demo.service.IBookService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
public class BookController {

	private final IBookService bookService;
	
	// Endpoint to get all books
    @GetMapping(value="",produces = "text/event-stream")
    public Flux<Book> getAllBooks() {
        return bookService.getAllBooks().delayElements(Duration.ofSeconds(1));
    }

    // Endpoint to get a book by ID
    @GetMapping(value="/{id}",produces = "text/event-stream")
    public Mono<ResponseEntity<Book>> getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id)
            .map(book -> ResponseEntity.ok(book))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    // Endpoint to save a new book
    @PostMapping(value="",produces = "text/event-stream")
    public Mono<ResponseEntity<Book>> saveBook(@RequestBody Book book) {
        return bookService.saveBook(book)
            .map(savedBook -> ResponseEntity.status(HttpStatus.CREATED).body(savedBook));
    }

    // Endpoint to delete a book by ID
    @DeleteMapping(value="/{id}",produces = "text/event-stream")
    public Mono<ResponseEntity<String>> deleteBookById(@PathVariable Integer id) {
        return bookService.deleteBookById(id)
            .map(message -> ResponseEntity.ok(message))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())));
    }
	
	
}
