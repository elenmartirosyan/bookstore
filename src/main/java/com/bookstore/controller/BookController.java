package com.bookstore.controller;

import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.entity.Book;
import com.bookstore.service.BookService;
import com.bookstore.service.dto.BookDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @param pageable the pageable object.
     * @return the response entity of the requested book in {@link BookDTO}.
     */
    @GetMapping()
    public ResponseEntity<List<BookDTO>> getAllBooks(Pageable pageable) {
        final Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
        );

        return ResponseEntity.ok(bookService.getAllBooks(pageRequest));
    }

    /**
     * API for creating a book with the given details.
     *
     * @param bookRequestDTO the request dto.
     * @return the newly created book dto in {@link BookDTO}.
     */
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookRequestDTO) {
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
    private ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId, @RequestBody BookDTO bookRequestDTO) {
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
    private ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
        try {
            bookService.deleteBook(bookId);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}