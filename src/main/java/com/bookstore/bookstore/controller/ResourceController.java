package com.bookstore.bookstore.controller;

import com.bookstore.bookstore.service.ResourceService;
import com.bookstore.bookstore.service.dto.GenreDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Class for managing static resources.
 */
@RestController
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * API for getting all the genres.
     *
     * @return the response entity with the list of genres in {@link GenreDTO}.
     */
    @GetMapping("/genre")
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        return ResponseEntity.ok(resourceService.getAllGenres());
    }
}
