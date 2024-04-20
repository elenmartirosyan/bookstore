package com.bookstore.repository;


import com.bookstore.repository.entity.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for the {@link Author} entity.
 */
public interface AuthorRepository extends CrudRepository<Author, Long>, PagingAndSortingRepository<Author, Long> {
}
