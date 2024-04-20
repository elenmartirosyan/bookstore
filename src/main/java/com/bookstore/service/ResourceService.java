package com.bookstore.service;

import com.bookstore.repository.GenreRepository;
import com.bookstore.service.dto.GenreDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for the static resources.
 */
@Service
public class ResourceService {

    private final GenreRepository genreRepository;

    public ResourceService(final GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Method to get all the genres.
     *
     * @return the list of {@link GenreDTO}.
     */
    public List<GenreDTO> getAllGenres() {
        return GenreDTO.mapEntitiesToDTOs(genreRepository.findAll());
    }
}
