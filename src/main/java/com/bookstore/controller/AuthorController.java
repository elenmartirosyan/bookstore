package com.bookstore.controller;

import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.entity.Author;
import com.bookstore.service.AuthorService;
import com.bookstore.service.dto.AuthorDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Class for managing {@link Author} related apis.
 */
@RestController
@RequestMapping("/author")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * API for getting all authors.
     *
     * @param pageable the pageable object.
     * @return the response entity with the list of authors in {@link com.bookstore.service.dto.AuthorDTO}.
     */
    @GetMapping()
    public ResponseEntity<List<AuthorDTO>> getAllAuthors(Pageable pageable) {
        final Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
        );

        return ResponseEntity.ok(authorService.getAllAuthors(pageRequest));
    }

    /**
     * API for getting all authors count.
     *
     * @return the response entity with the count of authors.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getAuthorsCount() {
        return ResponseEntity.ok(authorService.getAuthorsCount());
    }

    /**
     * API for getting an author for the given Id.
     *
     * @param authorId id of the requested author.
     * @return the response entity of the requested author in {@link AuthorDTO}.
     */
    @GetMapping("/{authorId}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long authorId) {
        final AuthorDTO author = authorService.getAuthorById(authorId);
        if (author == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(authorService.getAuthorById(authorId));
    }

    /**
     * API for creating an author with the given details.
     *
     * @param authorRequestDTO the request dto.
     * @return the newly created author dto in {@link AuthorDTO}.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody @Valid AuthorDTO authorRequestDTO) {
        if (authorRequestDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        authorRequestDTO.setId(null);
        final AuthorDTO createdAuthor = authorService.saveAuthor(authorRequestDTO);

        return ResponseEntity.ok(createdAuthor);
    }

    /**
     * API for updating existing author with the given details.
     *
     * @param authorId         the id of the author which needs to be updated.
     * @param authorRequestDTO the request dto.
     * @return the updated author dto in {@link AuthorDTO}.
     */
    @PutMapping("/{authorId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long authorId, @RequestBody @Valid AuthorDTO authorRequestDTO) {
        final AuthorDTO updated = authorService.updateAuthor(authorId, authorRequestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }


    /**
     * API for deleting an author for the given id.
     *
     * @param authorId the id of the author which needs to be deleted.
     * @return the ResponseEntity.
     */
    @DeleteMapping("/{authorId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long authorId) {
        try {
            authorService.deleteAuthor(authorId);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
