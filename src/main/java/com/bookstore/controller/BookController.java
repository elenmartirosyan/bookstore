package com.bookstore.controller;

import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.entity.Book;
import com.bookstore.service.BookService;
import com.bookstore.service.dto.BookDTO;
import com.bookstore.service.dto.BookSearchDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Class for managing {@link Book} related apis
 */
@RestController
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * API for getting a book for the given id.
     *
     * @param bookId id of the requested book.
     * @return the response entity of the requested book in {@link BookDTO}.
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long bookId) {
        final BookDTO book = bookService.getBookById(bookId);
        if (book == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(book);
    }

    /**
     * API for getting all books.
     *
     * @param bookSearchDTO dto object for search.
     * @param pageable the pageable object.
     * @return the response entity of the requested book in {@link BookDTO}.
     */
    @GetMapping()
    public ResponseEntity<List<BookDTO>> getAllBooks(BookSearchDTO bookSearchDTO, Pageable pageable) {
        final Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
        );

        return ResponseEntity.ok(bookService.getAllBooks(bookSearchDTO, pageRequest));
    }

    /**
     * API for getting the books count.
     *
     * @param bookSearchDTO dto object for search.
     * @return the response entity with the count of books.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getBooksCount(BookSearchDTO bookSearchDTO) {
        return ResponseEntity.ok(bookService.getBooksCount(bookSearchDTO));
    }

    /**
     * API for creating a book with the given details.
     *
     * @param bookRequestDTO the request dto.
     * @return the newly created book dto in {@link BookDTO}.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookDTO> createBook(@RequestBody @Valid BookDTO bookRequestDTO) {
        if (bookRequestDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        bookRequestDTO.setId(null);
        final BookDTO createdBook;
        try {
            createdBook = bookService.saveBook(bookRequestDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(createdBook);
    }

    /**
     * API for updating existing book with the given details.
     *
     * @param bookId         the id of the book which needs to be updated.
     * @param bookRequestDTO the request dto.
     * @return the updated book dto in {@link BookDTO}.
     */
    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId, @RequestBody @Valid BookDTO bookRequestDTO) {
        final BookDTO updated;
        try {
            updated = bookService.updateBook(bookId, bookRequestDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }


    /**
     * API for deleting the book for the given id.
     *
     * @param bookId the id of the book which needs to be deleted.
     * @return the ResponseEntity.
     */
    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
        try {
            bookService.deleteBook(bookId);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
