package com.bookstore;

import com.bookstore.repository.GenreRepository;
import com.bookstore.repository.entity.Genre;
import com.bookstore.service.ResourceService;
import com.bookstore.service.dto.GenreDTO;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;

/**
 * Test class for {@link ResourceService}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
class ResourceServiceTest {
    private ResourceService resourceService;
    private GenreRepository genreRepository;

    @BeforeEach
    public void setup() {
        genreRepository = EasyMock.createMock(GenreRepository.class);
        resourceService = new ResourceService(genreRepository);
    }

    @Test
    void getAllGenresSuccessTest() {
        reset(genreRepository);
        final Genre genre = new Genre();
        final Iterable<Genre> iterable = List.of(genre);
        expect(genreRepository.findAll()).andReturn(iterable);
        replay(genreRepository);
        final List<GenreDTO> response = resourceService.getAllGenres();
        verify(genreRepository);
        assertThat(response).isEqualTo(GenreDTO.mapEntitiesToDTOs(List.of(genre)));
    }


}