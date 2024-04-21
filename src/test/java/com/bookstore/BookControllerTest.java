package com.bookstore;

import com.bookstore.controller.BookController;
import com.bookstore.service.dto.AuthorDTO;
import com.bookstore.service.dto.BookDTO;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link BookController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
class BookControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void getAllBooksSuccessTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/book", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactly("The Da Vinci Code", "It", "unknown");
    }

    @Test
    void getAllBooksPaginatedSuccessTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/book?page=0&size=1&sort=id,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactly(3);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactly("unknown");
    }

    @Test
    void getAllBooksPaginatedSearchWithTitleSuccessTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/book?title=it&page=0&size=1&sort=id,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

        JSONArray ids = documentContext.read("$[*].id");
        assertThat(ids).containsExactly(2);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactly("It");
    }

    @Test
    void getAllBooksPaginatedSearchWithAuthorSuccessTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/book?authorIds=1&page=0&size=1&sort=id,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

        JSONArray ids = documentContext.read("$[*].id");
        assertThat(ids).containsExactly(2);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactly("It");
    }

    @Test
    void getAllBooksPaginatedSearchWithGenreSuccessTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/book?genreIds=3&page=0&size=1&sort=id,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

        JSONArray ids = documentContext.read("$[*].id");
        assertThat(ids).containsExactly(1);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactly("The Da Vinci Code");
    }

    @Test
    void getBookByIdSuccessTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/book/2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String title = documentContext.read("$.title");
        assertThat(title).isEqualTo("It");

        JSONArray genres = documentContext.read("$.listOfGenres");
        assertThat(genres.size()).isEqualTo(1);
        assertThat(genres.get(0).toString()).isEqualTo("{id=2, name=novel}");

        JSONArray authors = documentContext.read("$.listOfAuthors");
        assertThat(authors.size()).isEqualTo(1);
        assertThat(authors.get(0).toString()).isEqualTo("{id=1, name=Stephen, surname=King}");
    }

    @Test
    void getBookByIdDoesNotExistTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/book/100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void createBookSuccessTest() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("title1");
        ResponseEntity<BookDTO> createResponse = restTemplate
                .postForEntity("/book", bookDTO, BookDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        BookDTO createdBook = createResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/book/" + createdBook.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");

        assertThat(id).isNotNull();
        assertThat(title).isEqualTo(createdBook.getTitle());
    }

    @Test
    @DirtiesContext
    void createBookWithAuthorSuccessTest() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("title1");
        AuthorDTO author = new AuthorDTO();
        author.setId(1L);
        bookDTO.setListOfAuthors(Set.of(author));
        ResponseEntity<BookDTO> createResponse = restTemplate
                .postForEntity("/book", bookDTO, BookDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        BookDTO createdBook = createResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/book/" + createdBook.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");
        JSONArray authors = documentContext.read("$.listOfAuthors");

        assertThat(id).isNotNull();
        assertThat(title).isEqualTo(createdBook.getTitle());
        assertThat(authors.size()).isEqualTo(1);
        assertThat(authors.get(0).toString()).isEqualTo("{id=1, name=Stephen, surname=King}");
    }

    @Test
    @DirtiesContext
    void updateBookSuccessTest() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("updatedTitle");
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO);
        ResponseEntity<BookDTO> updateResponse = restTemplate
                .exchange("/book/1", HttpMethod.PUT, request, BookDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        BookDTO updatedBook = updateResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/book/" + updatedBook.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");

        assertThat(id).isNotNull();
        assertThat(title).isEqualTo(updatedBook.getTitle());
    }

    @Test
    @DirtiesContext
    void updateBookNotFoundTest() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("updatedTitle");
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO);
        ResponseEntity<BookDTO> updateResponse = restTemplate
                .exchange("/book/1000", HttpMethod.PUT, request, BookDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateResponse.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void deleteBookSuccessTest() {
        ResponseEntity<String> getResponseBeforeDelete = restTemplate
                .getForEntity("/book/2", String.class);
        assertThat(getResponseBeforeDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseBeforeDelete.getBody()).isNotNull();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/book/2", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isNull();

        ResponseEntity<String> getResponseAfterDelete = restTemplate
                .getForEntity("/book/2", String.class);
        assertThat(getResponseAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponseAfterDelete.getBody()).isNull();
    }
}