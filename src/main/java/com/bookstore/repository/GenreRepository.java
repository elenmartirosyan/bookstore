package com.bookstore.repository;

import com.bookstore.repository.entity.Genre;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for the {@link Genre} entity.
 */
public interface GenreRepository extends CrudRepository<Genre, Integer> {
}
