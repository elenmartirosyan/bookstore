package com.bookstore.bookstore.service.dto;

import com.bookstore.bookstore.repository.entity.Genre;

import java.util.ArrayList;
import java.util.List;

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

    public static GenreDTO mapEntityToDTO(final Genre entity) {
        if (entity == null)
            return null;
        GenreDTO dto = new GenreDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public static List<GenreDTO> mapEntitiesToDTOs(final Iterable<Genre> entities) {
        List<GenreDTO> dtos = new ArrayList<>();
        for (Genre entity : entities) {
            dtos.add(mapEntityToDTO(entity));
        }
        return dtos;
    }
}
