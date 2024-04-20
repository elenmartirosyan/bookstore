package com.bookstore;

import com.bookstore.controller.ResourceController;
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
 * Test class for {@link ResourceController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
class ResourceControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void getAllGenresSuccessTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/resources/genre", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray names = documentContext.read("$..name");
        assertThat(names).containsExactly("fiction", "novel", "mystery");
    }

}