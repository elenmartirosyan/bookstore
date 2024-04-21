package com.bookstore.service.dto;

import java.util.List;

/**
 * Data transfer object for the Book search functionality.
 */
public class BookSearchDTO {
    private String title;
    private List<Long> authorIds;
    private List<Integer> genreIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Long> authorIds) {
        this.authorIds = authorIds;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }
}
