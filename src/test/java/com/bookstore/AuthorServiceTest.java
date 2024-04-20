package com.bookstore;

import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.entity.Author;
import com.bookstore.service.AuthorService;
import com.bookstore.service.dto.AuthorDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link AuthorService}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
class AuthorServiceTest {
    private static final Long AUTHOR_ID = 1L;
    private AuthorService authorService;
    private AuthorRepository authorRepository;


    @BeforeEach
    public void setup() {
        authorRepository = EasyMock.createMock(AuthorRepository.class);
        authorService = new AuthorService(authorRepository);
    }

    @Test
    void getAuthorByIdSuccessTest() {
        reset(authorRepository);
        final Author author = new Author();
        author.setId(AUTHOR_ID);
        expect(authorRepository.findById(AUTHOR_ID)).andReturn(Optional.of(author));
        replay(authorRepository);
        final AuthorDTO response = authorService.getAuthorById(AUTHOR_ID);
        verify(authorRepository);
        assertThat(response).isEqualTo(AuthorDTO.mapEntityToDTO(author));
    }

    @Test
    void getAuthorByIdNoFoundTest() {
        reset(authorRepository);
        expect(authorRepository.findById(AUTHOR_ID)).andReturn(Optional.empty());
        replay(authorRepository);
        final AuthorDTO response = authorService.getAuthorById(AUTHOR_ID);
        verify(authorRepository);
        assertThat(response).isNull();
    }


    @Test
    void getAllAuthorsSuccessTest() {
        final PageRequest pageable = PageRequest.of(0, 1);
        reset(authorRepository);
        final Author author = new Author();
        final Page<Author> page = new PageImpl<>(List.of(author), pageable, 0);
        expect(authorRepository.findAll(pageable)).andReturn(page);
        replay(authorRepository);
        final List<AuthorDTO> response = authorService.getAllAuthors(pageable);
        verify(authorRepository);
        assertThat(response).isEqualTo(AuthorDTO.mapEntitiesToDTOs(List.of(author)));
    }

    @Test
    @DirtiesContext
    void saveAuthorSuccessTest() {
        final AuthorDTO requestDTO = new AuthorDTO();
        requestDTO.setName("name");
        requestDTO.setSurname("surname");

        expect(authorRepository.save(AuthorDTO.mapDTOToEntity(requestDTO))).andReturn(AuthorDTO.mapDTOToEntity(requestDTO));
        replay(authorRepository);
        final AuthorDTO response = authorService.saveAuthor(requestDTO);
        verify(authorRepository);
        assertThat(response).isEqualTo(requestDTO);
    }

    @Test
    void saveAuthorFailedTest() {
        final AuthorDTO response = authorService.saveAuthor(null);
        assertThat(response).isNull();
    }

    @Test
    @DirtiesContext
    void deleteAuthorSuccessTest() {
        expect(authorRepository.findById(AUTHOR_ID)).andReturn(Optional.of(new Author()));
        authorRepository.deleteById(AUTHOR_ID);
        expectLastCall();
        replay(authorRepository);
        authorService.deleteAuthor(AUTHOR_ID);
        verify(authorRepository);
    }

    @Test
    @DirtiesContext
    void deleteAuthorNotFoundTest() {
        expect(authorRepository.findById(AUTHOR_ID)).andReturn(Optional.empty());
        replay(authorRepository);
        final Throwable exception = assertThrows(NotFoundException.class, () -> authorService.deleteAuthor(AUTHOR_ID));
        assertThat("Author with id " + AUTHOR_ID + " does not exist").isEqualTo(exception.getMessage());
        verify(authorRepository);
    }

    @Test
    @DirtiesContext
    void updateAuthorSuccessTest() {
        final AuthorDTO requestDTO = new AuthorDTO();
        requestDTO.setName("updatedName");
        expect(authorRepository.findById(AUTHOR_ID)).andReturn(Optional.of(new Author()));
        expect(authorRepository.save(AuthorDTO.mapDTOToEntity(requestDTO))).andReturn(AuthorDTO.mapDTOToEntity(requestDTO));
        replay(authorRepository);
        final AuthorDTO response = authorService.updateAuthor(AUTHOR_ID, requestDTO);
        verify(authorRepository);
        assertThat(response).isEqualTo(requestDTO);
    }

    @Test
    @DirtiesContext
    void updateAuthorNotFoundTest() {
        final AuthorDTO requestDTO = new AuthorDTO();
        requestDTO.setName("updatedName");
        expect(authorRepository.findById(AUTHOR_ID)).andReturn(Optional.empty());
        replay(authorRepository);
        final AuthorDTO response = authorService.updateAuthor(AUTHOR_ID, requestDTO);
        verify(authorRepository);
        assertThat(response).isNull();
    }
}