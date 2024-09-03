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
