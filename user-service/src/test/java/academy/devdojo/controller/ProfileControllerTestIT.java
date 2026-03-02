package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.domain.Profile;
import academy.devdojo.response.ProfileGetResponse;
import academy.devdojo.response.ProfilePostResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileControllerTestIT {

    private static final String URL = "/v1/profiles";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProfileUtils profileUtils;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("GET /v1/profiles returns a list with all profiles")
    @Sql(value = "/sql/init_two_profiles.sql")
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenSuccessful() {
        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {};
        var responseEntity = testRestTemplate.exchange(URL, GET, null, typeReference);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull().isNotEmpty().doesNotContainNull();
        responseEntity.getBody().forEach(profile -> assertThat(profile).hasNoNullFieldsOrProperties());
    }

    @Test
    @DisplayName("GET /v1/profiles returns empty list when nothing is not found")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNothingIsNotFound() {
        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {};
        var responseEntity = testRestTemplate.exchange(URL, GET, null, typeReference);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull().isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("POST v1/profiles save a Profile when successful")
    void save_SaveAProfile_WhenSuccessful() throws Exception {
        var request = fileUtils.readSourceFile("/profiles/post-request-profiles-201.json");
        var profileEntity = buildHttpEntity(request);
        var responseEntity = testRestTemplate.exchange(URL, POST, profileEntity, ProfilePostResponse.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull().hasNoNullFieldsOrProperties();
    }

    private static HttpEntity<String> buildHttpEntity(String request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(request, httpHeaders);
    }

}