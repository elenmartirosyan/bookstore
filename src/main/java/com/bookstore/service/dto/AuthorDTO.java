package com.bookstore.service.dto;

import com.bookstore.repository.entity.Author;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data transfer object for the Author.
 */
public class AuthorDTO {
    private Long id;
    @NonNull
    private String name;
    @Nullable
    private String surname;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getSurname() {
        return surname;
    }

    public void setSurname(@Nullable String surname) {
        this.surname = surname;
    }

    @Nullable
    public static AuthorDTO mapEntityToDTO(final Author entity) {
        if (entity == null)
            return null;
        AuthorDTO dto = new AuthorDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        return dto;
    }

    @NonNull
    public static List<AuthorDTO> mapEntitiesToDTOs(final Iterable<Author> entities) {
        List<AuthorDTO> dtos = new ArrayList<>();
        for (Author entity : entities) {
            dtos.add(mapEntityToDTO(entity));
        }
        return dtos;
    }

    @Nullable
    public static Author mapDTOToEntity(final AuthorDTO dto) {
        if (dto == null)
            return null;
        Author entity = new Author();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorDTO authorDTO = (AuthorDTO) o;
        return Objects.equals(id, authorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
