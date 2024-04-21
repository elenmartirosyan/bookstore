package com.bookstore.service;

import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.entity.Book;
import com.bookstore.service.dto.AuthorDTO;
import com.bookstore.service.dto.BookDTO;
import com.bookstore.service.dto.GenreDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for the {@link Book}
 */
@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Method to get the book by the given Id.
     *
     * @param bookId the id of the book to get.
     * @return {@link BookDTO}.
     */
    public BookDTO getBookById(@NonNull final Long bookId) {
        final Optional<Book> book = bookRepository.findById(bookId);
        return book.map(BookDTO::mapEntityToDTO).orElse(null);
    }

    /**
     * Method to get all the books.
     *
     * @param pageable the pageable object.
     * @return the list of {@link BookDTO}.
     */
    public List<BookDTO> getAllBooks(@NonNull final Pageable pageable) {
        return BookDTO.mapEntitiesToDTOs(bookRepository.findAll(pageable).getContent());
    }

    /**
     * Method to save book with the given details.
     *
     * @param bookRequestDTO the request dto.
     * @return the saved book {@link BookDTO}.
     */
    public BookDTO saveBook(@NonNull final BookDTO bookRequestDTO) {
        final Book book = BookDTO.mapDTOToEntity(bookRequestDTO);
        if (book == null) {
            return null;
        }
        book.setCreationDate(Instant.now());
        return BookDTO.mapEntityToDTO(bookRepository.save(book));
    }

    /**
     * Method to update the existing book with the given details.
     *
     * @param bookId         the id of the book that needs to be updated.
     * @param bookRequestDTO the request dto.
     * @return the saved author {@link BookDTO}.
     */
    public BookDTO updateBook(Long bookId, BookDTO bookRequestDTO) {
        final Optional<Book> oBook = bookRepository.findById(bookId);
        if (oBook.isEmpty()) {
            return null;
        }

        Book existingBook = oBook.get();
        existingBook.setTitle(bookRequestDTO.getTitle());
        existingBook.setDescription(bookRequestDTO.getDescription());
        existingBook.setPrice(bookRequestDTO.getPrice());
        existingBook.setYear(bookRequestDTO.getYear());
        existingBook.setListOfAuthors(new HashSet<>(AuthorDTO.mapDTOsToEntities(bookRequestDTO.getListOfAuthors())));
        existingBook.setListOfGenres(new HashSet<>(GenreDTO.mapDTOsToEntities(bookRequestDTO.getListOfGenres())));
        existingBook = bookRepository.save(existingBook);

        return BookDTO.mapEntityToDTO(existingBook);
    }

    /**
     * Method to delete book by the given id.
     *
     * @param bookId the id of the book which needs to be deleted.
     */
    public void deleteBook(Long bookId) {
        final Optional<Book> oBook = bookRepository.findById(bookId);
        if (oBook.isEmpty()) {
            throw new NotFoundException("Book with id " + bookId + " does not exist");
        }
        bookRepository.deleteById(bookId);
    }
}
