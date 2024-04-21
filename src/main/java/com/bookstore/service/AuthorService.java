package com.bookstore.service;

import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.entity.Author;
import com.bookstore.service.dto.AuthorDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for the {@link Author}
 */
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(final AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Method to get all the authors.
     *
     * @param pageable the pageable object.
     * @return the list of {@link AuthorDTO}.
     */
    public List<AuthorDTO> getAllAuthors(@NonNull Pageable pageable) {
        return AuthorDTO.mapEntitiesToDTOs(authorRepository.findAll(pageable).getContent());
    }

    /**
     * Method to get the author by the given Id.
     *
     * @param id the id of the author to get.
     * @return {@link AuthorDTO}.
     */
    @Nullable
    public AuthorDTO getAuthorById(@NonNull Long id) {
        final Optional<Author> author = authorRepository.findById(id);
        return author.map(AuthorDTO::mapEntityToDTO).orElse(null);
    }


    /**
     * Method to save author with the given details.
     *
     * @param authorRequestDTO the request dto.
     * @return the saved author {@link AuthorDTO}.
     */
    public AuthorDTO saveAuthor(AuthorDTO authorRequestDTO) {
        final Author author = AuthorDTO.mapDTOToEntity(authorRequestDTO);
        if (author == null) {
            return null;
        }
        return AuthorDTO.mapEntityToDTO(authorRepository.save(author));
    }

    /**
     * Method to update the existing author with the given details.
     *
     * @param authorId         the id of the author that needs to be updated.
     * @param authorRequestDTO the request dto.
     * @return the saved author {@link AuthorDTO}.
     */
    public AuthorDTO updateAuthor(Long authorId, AuthorDTO authorRequestDTO) {
        final Optional<Author> oAuthor = authorRepository.findById(authorId);
        if (oAuthor.isEmpty()) {
            return null;
        }

        Author existingAuthor = oAuthor.get();
        existingAuthor.setName(authorRequestDTO.getName());
        existingAuthor.setSurname(authorRequestDTO.getSurname());

        existingAuthor = authorRepository.save(existingAuthor);
        return AuthorDTO.mapEntityToDTO(existingAuthor);
    }

    /**
     * Method to delete author by the given id.
     *
     * @param authorId the id of the author which needs to be deleted.
     */
    public void deleteAuthor(Long authorId) {
        final Optional<Author> oAuthor = authorRepository.findById(authorId);
        if (oAuthor.isEmpty()) {
            throw new NotFoundException("Author with id " + authorId + " does not exist");
        }
        authorRepository.deleteById(authorId);
    }
}
