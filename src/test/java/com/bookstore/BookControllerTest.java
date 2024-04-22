package com.bookstore;

import com.bookstore.controller.BookController;
import com.bookstore.service.dto.AuthorDTO;
import com.bookstore.service.dto.BookDTO;
import com.bookstore.service.dto.JwtDTO;
import com.bookstore.service.dto.SignInRequestDTO;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link BookController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
class BookControllerTest {
    private static boolean setupIsDone = false;
    private static String ADMIN_TOKEN;
    private static String USER_TOKEN;
    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        if (setupIsDone)
            return;
        SignInRequestDTO signInRequestDTO = new SignInRequestDTO("admin", "pass1");
        ResponseEntity<JwtDTO> response = restTemplate
                .postForEntity("/auth/signin", signInRequestDTO, JwtDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        ADMIN_TOKEN = response.getBody().accessToken();

        signInRequestDTO = new SignInRequestDTO("user", "pass2");
        response = restTemplate
                .postForEntity("/auth/signin", signInRequestDTO, JwtDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        USER_TOKEN = response.getBody().accessToken();
        setupIsDone = true;
    }

    @Test
    void getAllBooksSuccessTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        final ResponseEntity<String> response = restTemplate
                .exchange("/book", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactly("The Da Vinci Code", "It", "unknown");
    }

    @Test
    void getAllBooksAuthFailedTest() {
        final ResponseEntity<String> response = restTemplate
                .getForEntity("/book", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getAllBooksPaginatedSuccessTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        final ResponseEntity<String> response = restTemplate
                .exchange("/book?page=0&size=1&sort=id,desc", HttpMethod.GET, request, String.class);
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
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/book?title=it&page=0&size=1&sort=id,desc", HttpMethod.GET, request, String.class);
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
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/book?authorIds=1&page=0&size=1&sort=id,desc", HttpMethod.GET, request, String.class);
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
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/book?genreIds=3&page=0&size=1&sort=id,desc", HttpMethod.GET, request, String.class);
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
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/book/2", HttpMethod.GET, request, String.class);
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
    void getBookByIdAuthFailedTest() {
        final ResponseEntity<String> response = restTemplate
                .getForEntity("/book/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getBookByIdDoesNotExistTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/book/100", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void createBookSuccessTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("title1");
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO, headers);
        ResponseEntity<BookDTO> createResponse = restTemplate
                .postForEntity("/book", request, BookDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        BookDTO createdBook = createResponse.getBody();
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> getResponse = restTemplate
                .exchange("/book/" + createdBook.getId(), HttpMethod.GET, request, String.class);
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("title1");
        AuthorDTO author = new AuthorDTO();
        author.setId(1L);
        bookDTO.setListOfAuthors(Set.of(author));
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO, headers);
        ResponseEntity<BookDTO> createResponse = restTemplate
                .postForEntity("/book", request, BookDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        BookDTO createdBook = createResponse.getBody();
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> getResponse = restTemplate
                .exchange("/book/" + createdBook.getId(), HttpMethod.GET, request, String.class);
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
    void createBookFailedUnauthorizedWithUserTokenTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("title1");
        AuthorDTO author = new AuthorDTO();
        author.setId(1L);
        bookDTO.setListOfAuthors(Set.of(author));
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO, headers);
        ResponseEntity<BookDTO> createResponse = restTemplate
                .postForEntity("/book", request, BookDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createBookFailedUnauthorizedTest() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("title1");
        AuthorDTO author = new AuthorDTO();
        author.setId(1L);
        bookDTO.setListOfAuthors(Set.of(author));
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO);
        ResponseEntity<BookDTO> createResponse = restTemplate
                .postForEntity("/book", request, BookDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void updateBookSuccessTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("updatedTitle");
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO, headers);
        ResponseEntity<BookDTO> updateResponse = restTemplate.
                exchange("/book/1", HttpMethod.PUT, request, BookDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        BookDTO updatedBook = updateResponse.getBody();

        HttpEntity<String> requestForGet = new HttpEntity<>(null, headers);
        ResponseEntity<String> getResponse = restTemplate
                .exchange("/book/" + updatedBook.getId(), HttpMethod.GET, requestForGet, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");

        assertThat(id).isNotNull();
        assertThat(title).isEqualTo(updatedBook.getTitle());
    }

    @Test
    void updateBookNotFoundTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("updatedTitle");
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO, headers);
        ResponseEntity<BookDTO> updateResponse = restTemplate
                .exchange("/book/1000", HttpMethod.PUT, request, BookDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateResponse.getBody()).isNull();
    }

    @Test
    void updateBookFailedUnauthorizedWithUserTokenTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("updatedTitle");
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO, headers);
        ResponseEntity<BookDTO> updateResponse = restTemplate
                .exchange("/book/1", HttpMethod.PUT, request, BookDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(updateResponse.getBody()).isNull();
    }

    @Test
    void updateBookFailedUnauthorizedTest() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("updatedTitle");
        HttpEntity<BookDTO> request = new HttpEntity<>(bookDTO);
        ResponseEntity<BookDTO> updateResponse = restTemplate
                .exchange("/book/1", HttpMethod.PUT, request, BookDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(updateResponse.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void deleteBookSuccessTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> getResponseBeforeDelete = restTemplate
                .exchange("/book/2", HttpMethod.GET, request, String.class);
        assertThat(getResponseBeforeDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseBeforeDelete.getBody()).isNotNull();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/book/2", HttpMethod.DELETE, request, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isNull();

        ResponseEntity<String> getResponseAfterDelete = restTemplate
                .exchange("/book/2", HttpMethod.GET, request, String.class);
        assertThat(getResponseAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponseAfterDelete.getBody()).isNull();
    }

    @Test
    void deleteBookUnauthorizedWithUserTokenTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/book/2", HttpMethod.DELETE, request, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(deleteResponse.getBody()).isNull();
    }

    @Test
    void deleteBookUnauthorizedTest() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/book/2", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(deleteResponse.getBody()).isNull();
    }
}