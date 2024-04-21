package com.bookstore.service.dto;

import com.bookstore.repository.entity.Genre;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data transfer object for the Genre.
 */
public class GenreDTO {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public static GenreDTO mapEntityToDTO(final Genre entity) {
        if (entity == null)
            return null;
        final GenreDTO dto = new GenreDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    @NonNull
    public static List<GenreDTO> mapEntitiesToDTOs(final Iterable<Genre> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        final List<GenreDTO> dtos = new ArrayList<>();
        for (Genre entity : entities) {
            dtos.add(mapEntityToDTO(entity));
        }
        return dtos;
    }

    @Nullable
    public static Genre mapDTOToEntity(final GenreDTO dto) {
        if (dto == null)
            return null;
        final Genre entity = new Genre();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    @NonNull
    public static List<Genre> mapDTOsToEntities(final Iterable<GenreDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        final List<Genre> entities = new ArrayList<>();
        for (GenreDTO dto : dtos) {
            entities.add(mapDTOToEntity(dto));
        }
        return entities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreDTO genreDTO = (GenreDTO) o;
        return Objects.equals(id, genreDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
