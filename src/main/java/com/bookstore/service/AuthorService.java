package com.bookstore.service;

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
 * Service layer for the {@link com.bookstore.repository.entity.Author}
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
     * @return the list of {@link AuthorDTO}.
     */
    public List<AuthorDTO> getAllAuthors(Pageable pageable) {
        return AuthorDTO.mapEntitiesToDTOs(authorRepository.findAll(pageable).getContent());
    }

    /**
     * Method to get the author by the given Id.
     *
     * @return {@link AuthorDTO}.
     */
    @Nullable
    public AuthorDTO getAuthorById(@NonNull Long id) {
        final Optional<Author> author = authorRepository.findById(id);
        return author.map(AuthorDTO::mapEntityToDTO).orElse(null);
    }
}
