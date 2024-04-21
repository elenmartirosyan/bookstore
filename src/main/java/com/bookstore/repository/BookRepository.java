package com.bookstore.repository;

import com.bookstore.repository.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for the {@link Book} entity.
 */
public interface BookRepository extends CrudRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {
    @Query("select b from Book b left join b.listOfAuthors a left join b.listOfGenres g " +
            " where (:title = '' or (lower(b.title) like concat('%',lower(:title),'%'))) " +
            " AND (COALESCE(:authorIds, NULL) IS NULL OR  a.id in (:authorIds))" +
            " AND (COALESCE(:genreIds, NULL) IS NULL OR  g.id in (:genreIds))")
    Page<Book> findAll(@Param("title") String title, @Param("authorIds") List<Long> authorIds,
                       @Param("genreIds") List<Integer> genreIds, Pageable pageable);
}
