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
