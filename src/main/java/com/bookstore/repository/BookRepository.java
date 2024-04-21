package com.bookstore.repository;

import com.bookstore.repository.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for the {@link Book} entity.
 */
public interface BookRepository extends CrudRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {

}
