package com.bookstore;

import com.bookstore.controller.AuthorController;
import com.bookstore.service.dto.AuthorDTO;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link AuthorController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
public class AuthorControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void getAllAuthorsSuccessTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/author", String.class);
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
    void getAllAuthorsSuccessWithPaginationTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/author?page=0&size=1&sort=id,desc", String.class);
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
        ResponseEntity<String> response = restTemplate
                .getForEntity("/author/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Stephen");

        String surname = documentContext.read("$.surname");
        assertThat(surname).isEqualTo("King");
    }

    @Test
    void getAuthorByIdSuccessNullSurnameTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/author/4", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Lewis");

        String surname = documentContext.read("$.surname");
        assertThat(surname).isNull();
    }

    @Test
    void getAuthorByIdDoesNotExistTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/author/100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void createAuthorSuccessTest() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("name");
        authorDTO.setSurname("surname");
        ResponseEntity<AuthorDTO> createResponse = restTemplate
                .postForEntity("/author", authorDTO, AuthorDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        AuthorDTO createdAuthor = createResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/author/" + createdAuthor.getId(), String.class);
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
    @DirtiesContext
    void createAuthorNoSurnameSuccessTest() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("name");
        ResponseEntity<AuthorDTO> createResponse = restTemplate
                .postForEntity("/author", authorDTO, AuthorDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        AuthorDTO createdAuthor = createResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/author/" + createdAuthor.getId(), String.class);
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
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("updatedName");
        HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO);
        ResponseEntity<AuthorDTO> updateResponse = restTemplate
                .exchange("/author/1", HttpMethod.PUT, request, AuthorDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        AuthorDTO updatedAuthor = updateResponse.getBody();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/author/" + updatedAuthor.getId(), String.class);
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
    void updateAuthorNotFoundTest() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("updatedName");
        HttpEntity<AuthorDTO> request = new HttpEntity<>(authorDTO);
        ResponseEntity<AuthorDTO> updateResponse = restTemplate
                .exchange("/author/1000", HttpMethod.PUT, request, AuthorDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateResponse.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void deleteAuthorSuccessTest() {
        ResponseEntity<String> getResponseBeforeDelete = restTemplate
                .getForEntity("/author/2", String.class);
        assertThat(getResponseBeforeDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseBeforeDelete.getBody()).isNotNull();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/author/2", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isNull();

        ResponseEntity<String> getResponseAfterDelete = restTemplate
                .getForEntity("/author/2", String.class);
        assertThat(getResponseAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponseAfterDelete.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void deleteAuthorNotAllowedTest() {
        ResponseEntity<String> getResponseBeforeDelete = restTemplate
                .getForEntity("/author/1", String.class);
        assertThat(getResponseBeforeDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseBeforeDelete.getBody()).isNotNull();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/author/1", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(deleteResponse.getBody()).isNull();
    }
}
