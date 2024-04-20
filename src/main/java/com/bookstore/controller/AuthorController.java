package com.bookstore.controller;

import com.bookstore.repository.entity.Author;
import com.bookstore.service.AuthorService;
import com.bookstore.service.dto.AuthorDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Class for managing {@link Author} related apis
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
     * @return the response entity with the list of author in {@link com.bookstore.service.dto.AuthorDTO}.
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
     * API for getting an author for the given Id.
     *
     * @param authorId id of the requested author.
     * @return the response entity of the requested author in {@link com.bookstore.service.dto.AuthorDTO}.
     */
    @GetMapping("/{authorId}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long authorId) {
        final AuthorDTO author = authorService.getAuthorById(authorId);
        if (author == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(authorService.getAuthorById(authorId));
    }
}
