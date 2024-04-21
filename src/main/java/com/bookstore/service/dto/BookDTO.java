package com.bookstore.service.dto;

import com.bookstore.repository.entity.Book;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.*;

/**
 * Data transfer object for the Book.
 */
public class BookDTO {
    private Long id;
    @NonNull
    private String title;
    @Nullable
    private String description;
    @Nullable
    private Double price;
    @Nullable
    private Integer year;
    @NonNull
    private Instant creationDate;

    private Set<GenreDTO> listOfGenres;
    private Set<AuthorDTO> listOfAuthors;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public Double getPrice() {
        return price;
    }

    public void setPrice(@Nullable Double price) {
        this.price = price;
    }

    @Nullable
    public Integer getYear() {
        return year;
    }

    public void setYear(@Nullable Integer year) {
        this.year = year;
    }

    @NonNull
    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(@NonNull Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Set<GenreDTO> getListOfGenres() {
        return listOfGenres;
    }

    public void setListOfGenres(Set<GenreDTO> listOfGenres) {
        this.listOfGenres = listOfGenres;
    }

    public Set<AuthorDTO> getListOfAuthors() {
        return listOfAuthors;
    }

    public void setListOfAuthors(Set<AuthorDTO> listOfAuthors) {
        this.listOfAuthors = listOfAuthors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDTO bookDTO = (BookDTO) o;
        return Objects.equals(id, bookDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", year=" + year +
                ", creationDate=" + creationDate +
                '}';
    }

    @Nullable
    public static BookDTO mapEntityToDTO(final Book entity) {
        if (entity == null)
            return null;
        final BookDTO dto = new BookDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setYear(entity.getYear());
        dto.setPrice(entity.getPrice());
        dto.setCreationDate(entity.getCreationDate());
        dto.setListOfAuthors(new HashSet<>(AuthorDTO.mapEntitiesToDTOs(entity.getListOfAuthors())));
        dto.setListOfGenres(new HashSet<>(GenreDTO.mapEntitiesToDTOs(entity.getListOfGenres())));
        return dto;
    }

    @NonNull
    public static List<BookDTO> mapEntitiesToDTOs(final Iterable<Book> entities) {
        final List<BookDTO> dtos = new ArrayList<>();
        for (Book entity : entities) {
            dtos.add(mapEntityToDTO(entity));
        }
        return dtos;
    }

    @Nullable
    public static Book mapDTOToEntity(final BookDTO dto) {
        if (dto == null)
            return null;
        final Book entity = new Book();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setYear(dto.getYear());
        entity.setPrice(dto.getPrice());
        entity.setCreationDate(dto.getCreationDate());
        entity.setListOfAuthors(new HashSet<>(AuthorDTO.mapDTOsToEntities(dto.getListOfAuthors())));
        entity.setListOfGenres(new HashSet<>(GenreDTO.mapDTOsToEntities(dto.getListOfGenres())));
        return entity;
    }

    @NonNull
    public static List<Book> mapDTOsToEntities(final Iterable<BookDTO> dtos) {
        final List<Book> entities = new ArrayList<>();
        for (BookDTO dto : dtos) {
            entities.add(mapDTOToEntity(dto));
        }
        return entities;
    }
}
