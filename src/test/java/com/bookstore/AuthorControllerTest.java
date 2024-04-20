package com.bookstore;

import com.bookstore.controller.AuthorController;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}
