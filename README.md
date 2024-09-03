# FAKE BOOK API

## Overview

The Book Management API is a reactive RESTful service designed to manage book records. It is built using Spring Boot and incorporates Project Reactor for reactive programming. The API supports CRUD operations and real-time streaming updates with Server-Sent Events (SSE).

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
