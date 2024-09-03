package com.luv2code.demo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.luv2code.demo.entity.Book;

@Repository
public interface BookRepository extends ReactiveCrudRepository<Book, Integer> {

}
