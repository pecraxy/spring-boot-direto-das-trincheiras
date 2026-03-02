package academy.devdojo.controller;

import academy.devdojo.response.ProfileGetResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileControllerTestIT {

    private static final String URL = "/v1/profiles";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("GET /v1/profiles returns a list with all profiles")
    @Sql(value = "/sql/init_two_profiles.sql")
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenSuccessful(){
        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {};
        var responseEntity = testRestTemplate.exchange(URL, HttpMethod.GET, null, typeReference);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull().isNotEmpty().doesNotContainNull();
        responseEntity.getBody().forEach(profile -> assertThat(profile).hasNoNullFieldsOrProperties());
    }

}