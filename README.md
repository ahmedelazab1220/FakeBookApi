# FAKE BOOK API

## Overview

The Book Management API is a reactive RESTful service designed to manage book records. It is built using Spring Boot and incorporates Project Reactor for reactive programming.

## Features

- **`Get all books`**: Stream a list of all books with real-time updates.
- **`Get a book by ID`**: Retrieve details of a specific book by its ID.
- **`Save a new book`**: Add a new book to the collection.
- **`Delete a book by ID`**: Remove a book from the collection by its ID.

## Technologies Used

- **`Spring Boot`**: Framework for building the application.
- **`Spring Data R2DBC`**: Provides reactive data access with relational databases.
- **`Project Reactor`**: Supports reactive programming with Flux and Mono.
- **`MySQL Database`**: In-memory database for development and testing (can be replaced with other databases).

## Project Structure

- Book Entity

```java

package com.luv2code.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    private Integer id;

    @Column(value="author")
    private String author;

    @Column(value="description")
    private String description;

    @Column(value="cover_image")
    private String coverImage;

    @Column(value="title")
    private String title;

    @Column(value="publication_year")
    private Integer publicationYear;
}

```

- Purpose: Represents the book entity mapped to the books table in the database.
- Annotations:

  - _@Table_ : Specifies the table name.
  - _@Id_ : Indicates the primary key field.
  - _@Column_ : Maps fields to columns in the table.

- Book Repository

```java

package com.luv2code.demo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.entity.Book;

@Repository
public interface BookRepository extends ReactiveCrudRepository<Book, Integer> {

}

```

- Purpose: Provides reactive data access methods for Book entities.
- Annotations:

  - _@Repository_: Indicates that this is a repository component.
  - Extends ReactiveCrudRepository for CRUD operations.

- Book Service Interface

```java

package com.luv2code.demo.service;

import com.luv2code.demo.entity.Book;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBookService {

    Flux<Book> getAllBooks();

    Mono<Book> getBookById(Integer bookId);

    Mono<String> deleteBookById(Integer bookId);

    Mono<Book> saveBook(Book book);

}


```

- Purpose: Defines the contract for the book service with CRUD operations.
- Methods:

  - _getAllBooks()_ : Retrieves a stream of all books.
  - _getBookById(Integer bookId)_ : Retrieves a single book by ID.
  - _deleteBookById(Integer bookId)_ : Deletes a book by ID and returns a status message.
  - _saveBook(Book book)_ : Saves a new book and returns the saved entity.

- Book Service Implementation

```java

package com.luv2code.demo.service.impl;

import org.springframework.stereotype.Service;

import com.luv2code.demo.entity.Book;
import com.luv2code.demo.repository.BookRepository;
import com.luv2code.demo.service.IBookService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class BookService implements IBookService {

    private final BookRepository bookRepository;

    @Override
    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Mono<Book> getBookById(Integer bookId) {
        return bookRepository.findById(bookId).switchIfEmpty(Mono.error(new RuntimeException("Book Not Found")));
    }

    @Override
    public Mono<Book> saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Mono<String> deleteBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .flatMap(book -> bookRepository.delete(book).then(Mono.just("Book Deleted Successfully!")))
                .switchIfEmpty(Mono.error(new RuntimeException("Book Not Found")));
    }
}


```

- Purpose: Implements the IBookService interface and interacts with the BookRepository.
- Methods:

  - _getAllBooks()_ : Returns all books from the repository.
  - _getBookById(Integer bookId)_ : Retrieves a book by ID or returns an error if not found.
  - _saveBook(Book book)_ : Saves the book to the repository.
  - _deleteBookById(Integer bookId)_ : Deletes the book if it exists and returns a success message.

- Book Controller

```java

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
    @GetMapping(value="", produces = "text/event-stream")
    public Flux<Book> getAllBooks() {
        return bookService.getAllBooks().delayElements(Duration.ofSeconds(1));
    }

    // Endpoint to get a book by ID
    @GetMapping(value="/{id}", produces = "text/event-stream")
    public Mono<ResponseEntity<Book>> getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id)
            .map(book -> ResponseEntity.ok(book))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    // Endpoint to save a new book
    @PostMapping(value="", produces = "text/event-stream")
    public Mono<ResponseEntity<Book>> saveBook(@RequestBody Book book) {
        return bookService.saveBook(book)
            .map(savedBook -> ResponseEntity.status(HttpStatus.CREATED).body(savedBook));
    }

    // Endpoint to delete a book by ID
    @DeleteMapping(value="/{id}", produces = "text/event-stream")
    public Mono<ResponseEntity<String>> deleteBookById(@PathVariable Integer id) {
        return bookService.deleteBookById(id)
            .map(message -> ResponseEntity.ok(message))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())));
    }
}


```

- Purpose: Provides RESTful endpoints for interacting with book resources.
- Endpoints:
  - **`GET /api/v1/books`** : Streams all books with a 1-second delay between elements.
  - **`GET /api/v1/books/{id}`** : Retrieves a book by its ID.
  - **`POST /api/v1/books`** : Adds a new book to the collection.
  - **`DELETE /api/v1/books/{id}`** : Deletes a book by its ID.

## Running the Application

- Clone the Repository

  - git clone https://github.com/ahmedelazab1220/FakeBookApi.git
  - cd FakeBookApi

- Build the Project

  - mvn clean install
  - mvn spring-boot:run

The application will start and listen on port `8080` by default.

## Development and Contribution

- Fork the Repository and create a feature branch.
- Commit Your Changes with descriptive messages.
- Push to Your Fork and create a pull request.
- For any issues or feature requests, please use the <a href="https://github.com/features/issues"> GitHub Issues </a> page.

## License

This project is licensed under the Apache License 2.0 - see the <a href="https://github.com/ahmedelazab1220/FakeBookApi/blob/main/LICENSE"> LICENSE </a> file for details.

## Conclusion

The Book Management API efficiently handles book records using a reactive, non-blocking approach with Spring Boot and Project Reactor. supports full CRUD operations, ensuring scalable and responsive interactions.
