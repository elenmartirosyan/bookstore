package com.bookstore;

import com.bookstore.controller.AuthorController;
import com.bookstore.service.dto.AuthorDTO;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link AuthorController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
public class AuthorControllerTest {
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
    void getAllAuthorsSuccessTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/author", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(4);

        JSONArray names = documentContext.read("$..name");
        assertThat(names).containsExactly("Stephen", "Nicolas", "Dan", "Lewis");

        JSONArray surnames = documentContext.read("$..surname");
        assertThat(surnames).containsExactly("King", "Sparks", "Brown", null);
    }

    @Test
    void getAllAuthorsUnauthorizedTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/author", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getAllAuthorsSuccessWithPaginationTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/author?page=0&size=1&sort=id,desc", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactly(4);

        JSONArray names = documentContext.read("$..name");
        assertThat(names).containsExactly("Lewis");
    }

    @Test
    void getAuthorByIdSuccessTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/author/1", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Stephen");

        String surname = documentContext.read("$.surname");
        assertThat(surname).isEqualTo("King");
    }

    @Test
    void getAuthorByIdUnauthorizedTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/author/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getAuthorByIdSuccessNullSurnameTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/author/4", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Lewis");

        String surname = documentContext.read("$.surname");
        assertThat(surname).isNull();
    }

    @Test
    void getAuthorByIdDoesNotExistTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/author/100", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void createAuthorSuccessTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("name");
        authorDTO.setSurname("surname");
        final HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO, headers);
        ResponseEntity<AuthorDTO> createResponse = restTemplate
                .postForEntity("/author", request, AuthorDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        AuthorDTO createdAuthor = createResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .exchange("/author/" + createdAuthor.getId(), HttpMethod.GET, request, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        String surname = documentContext.read("$.surname");

        assertThat(id).isNotNull();
        assertThat(name).isEqualTo(createdAuthor.getName());
        assertThat(surname).isEqualTo(createdAuthor.getSurname());
    }

    @Test
    void createAuthorUnauthorizedTest() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("name");
        authorDTO.setSurname("surname");
        final HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO);
        ResponseEntity<AuthorDTO> createResponse = restTemplate
                .postForEntity("/author", request, AuthorDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createAuthorUnauthorizedWithUserTokenTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("name");
        authorDTO.setSurname("surname");
        final HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO, headers);
        ResponseEntity<AuthorDTO> createResponse = restTemplate
                .postForEntity("/author", request, AuthorDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void createAuthorNoSurnameSuccessTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("name");
        final HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO, headers);
        ResponseEntity<AuthorDTO> createResponse = restTemplate
                .postForEntity("/author", request, AuthorDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        AuthorDTO createdAuthor = createResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .exchange("/author/" + createdAuthor.getId(), HttpMethod.GET, request, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        String surname = documentContext.read("$.surname");

        assertThat(id).isNotNull();
        assertThat(name).isEqualTo(createdAuthor.getName());
        assertThat(surname).isNull();
    }

    @Test
    @DirtiesContext
    void updateAuthorSuccessTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("updatedName");
        HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO, headers);
        ResponseEntity<AuthorDTO> updateResponse = restTemplate
                .exchange("/author/1", HttpMethod.PUT, request, AuthorDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        AuthorDTO updatedAuthor = updateResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .exchange("/author/" + updatedAuthor.getId(), HttpMethod.GET, request, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        String surname = documentContext.read("$.surname");

        assertThat(id).isNotNull();
        assertThat(name).isEqualTo(updatedAuthor.getName());
        assertThat(surname).isEqualTo(updatedAuthor.getSurname());
    }

    @Test
    @DirtiesContext
    void updateAuthorUnauthorizedTest() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("updatedName");
        HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO);
        ResponseEntity<AuthorDTO> updateResponse = restTemplate
                .exchange("/author/1", HttpMethod.PUT, request, AuthorDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void updateAuthorUnauthorizedWithUserTokenTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("updatedName");
        HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO, headers);
        ResponseEntity<AuthorDTO> updateResponse = restTemplate
                .exchange("/author/1", HttpMethod.PUT, request, AuthorDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void updateAuthorNotFoundTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("updatedName");
        HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO, headers);
        ResponseEntity<AuthorDTO> updateResponse = restTemplate
                .exchange("/author/1000", HttpMethod.PUT, request, AuthorDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateResponse.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void deleteAuthorSuccessTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> getResponseBeforeDelete = restTemplate
                .exchange("/author/2", HttpMethod.GET, request, String.class);
        assertThat(getResponseBeforeDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseBeforeDelete.getBody()).isNotNull();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/author/2", HttpMethod.DELETE, request, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isNull();

        ResponseEntity<String> getResponseAfterDelete = restTemplate
                .exchange("/author/2", HttpMethod.GET, request, String.class);
        assertThat(getResponseAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponseAfterDelete.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void deleteAuthorNotAllowedTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_TOKEN);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> getResponseBeforeDelete = restTemplate
                .exchange("/author/1", HttpMethod.GET, request, String.class);
        assertThat(getResponseBeforeDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseBeforeDelete.getBody()).isNotNull();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/author/1", HttpMethod.DELETE, request, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(deleteResponse.getBody()).isNull();
    }

    @Test
    void deleteAuthorUnauthorizedTest() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/author/1", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void deleteAuthorNotUnauthorizedWithUserTokenTest() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", USER_TOKEN);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/author/1", HttpMethod.DELETE, request, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(deleteResponse.getBody()).isNull();
    }
}
