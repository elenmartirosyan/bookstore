package com.bookstore;

import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.entity.Book;
import com.bookstore.service.BookService;
import com.bookstore.service.dto.BookDTO;
import com.bookstore.service.dto.BookSearchDTO;
import com.bookstore.service.dto.GenreDTO;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link BookService}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
class BookServiceTest {

    private static final Long BOOK_ID = 1L;
    private BookService bookService;
    private BookRepository bookRepository;


    @BeforeEach
    public void setup() {
        bookRepository = EasyMock.createMock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void getBookByIdSuccessTest() {
        reset(bookRepository);
        final Book book = new Book();
        book.setId(BOOK_ID);
        expect(bookRepository.findById(BOOK_ID)).andReturn(Optional.of(book));
        replay(bookRepository);
        final BookDTO response = bookService.getBookById(BOOK_ID);
        verify(bookRepository);
        assertThat(response).isEqualTo(BookDTO.mapEntityToDTO(book));
    }

    @Test
    void getBookByIdNotFoundTest() {
        reset(bookRepository);
        final Book book = new Book();
        book.setId(BOOK_ID);
        expect(bookRepository.findById(BOOK_ID)).andReturn(Optional.empty());
        replay(bookRepository);
        final BookDTO response = bookService.getBookById(BOOK_ID);
        verify(bookRepository);
        assertThat(response).isNull();
    }

    @Test
    void getAllBooksSuccessTest() {
        final BookSearchDTO bookSearchDTO = new BookSearchDTO();
        final PageRequest pageable = PageRequest.of(0, 1);
        reset(bookRepository);
        final Book book = new Book();
        final Page<Book> page = new PageImpl<>(List.of(book), pageable, 0);
        expect(bookRepository.findAll("", null, null, pageable)).andReturn(page);
        replay(bookRepository);
        final List<BookDTO> response = bookService.getAllBooks(bookSearchDTO, pageable);
        verify(bookRepository);
        assertThat(response).isEqualTo(BookDTO.mapEntitiesToDTOs(List.of(book)));
    }

    @Test
    @DirtiesContext
    void saveBookSuccessTest() {
        final BookDTO requestDTO = new BookDTO();
        requestDTO.setTitle("title");
        GenreDTO genre = new GenreDTO();
        genre.setId(1);
        requestDTO.setListOfGenres(Set.of(genre));
        expect(bookRepository.save(BookDTO.mapDTOToEntity(requestDTO))).andReturn(BookDTO.mapDTOToEntity(requestDTO));
        replay(bookRepository);
        final BookDTO response = bookService.saveBook(requestDTO);
        verify(bookRepository);
        assertThat(response).isEqualTo(requestDTO);
    }

    @Test
    void saveBookFailedTest() {
        final BookDTO response = bookService.saveBook(null);
        assertThat(response).isNull();
    }

    @Test
    @DirtiesContext
    void deleteBookSuccessTest() {
        expect(bookRepository.findById(BOOK_ID)).andReturn(Optional.of(new Book()));
        bookRepository.deleteById(BOOK_ID);
        expectLastCall();
        replay(bookRepository);
        bookService.deleteBook(BOOK_ID);
        verify(bookRepository);
    }

    @Test
    @DirtiesContext
    void deleteBookNotFoundTest() {
        expect(bookRepository.findById(BOOK_ID)).andReturn(Optional.empty());
        replay(bookRepository);
        final Throwable exception = assertThrows(NotFoundException.class, () -> bookService.deleteBook(BOOK_ID));
        assertThat("Book with id " + BOOK_ID + " does not exist").isEqualTo(exception.getMessage());
        verify(bookRepository);
    }

    @Test
    @DirtiesContext
    void updateBookSuccessTest() {
        final BookDTO requestDTO = new BookDTO();
        requestDTO.setTitle("updatedTitle");
        expect(bookRepository.findById(BOOK_ID)).andReturn(Optional.of(new Book()));
        expect(bookRepository.save(BookDTO.mapDTOToEntity(requestDTO))).andReturn(BookDTO.mapDTOToEntity(requestDTO));
        replay(bookRepository);
        final BookDTO response = bookService.updateBook(BOOK_ID, requestDTO);
        verify(bookRepository);
        assertThat(response).isEqualTo(requestDTO);
    }

    @Test
    @DirtiesContext
    void updateBookNotFoundTest() {
        final BookDTO requestDTO = new BookDTO();
        requestDTO.setTitle("updatedTitle");
        expect(bookRepository.findById(BOOK_ID)).andReturn(Optional.empty());
        replay(bookRepository);
        final BookDTO response = bookService.updateBook(BOOK_ID, requestDTO);
        verify(bookRepository);
        assertThat(response).isNull();
    }

}