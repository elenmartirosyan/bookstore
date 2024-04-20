package com.bookstore.bookstore.repository;

import com.bookstore.bookstore.repository.entity.Genre;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for the {@link Genre} entity.
 */
public interface GenreRepository extends CrudRepository<Genre, Integer> {
}
