package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.config.TestcontainersConfiguration;
import academy.devdojo.response.ProfileGetResponse;
import academy.devdojo.response.ProfilePostResponse;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestcontainersConfiguration.class)
class ProfileControllerTestIT {

    private static final String URL = "/v1/profiles";

    @Autowired
    private TestRestTemplate testRestTemplate;

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

//    @Test
//    @DisplayName("GET /v1/profiles returns empty list when nothing is not found")
//    @Order(2)
//    void findAll_ReturnsEmptyList_WhenNothingIsNotFound() {
//        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {};
//        var responseEntity = testRestTemplate.exchange(URL, GET, null, typeReference);
//        assertThat(responseEntity).isNotNull();
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isNotNull().isEmpty();
//    }

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

    @ParameterizedTest
    @MethodSource("postProfileBadRequestSource")
    @Order(4)
    @DisplayName("POST v1/profiles returns bad request when fields are empty or invalid")
    void save_ReturnsBadRequest_WhenFieldsAreEmptyOrInvalid(String requestFile, String responseFile) throws Exception {
        var request = fileUtils.readSourceFile("/profiles/%s".formatted(requestFile));
        var expectedResponse = fileUtils.readSourceFile("/profiles/%s".formatted(responseFile));

        var profileEntity = buildHttpEntity(request);
        var responseEntity = testRestTemplate.exchange(URL, POST, profileEntity, String.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        JsonAssertions.assertThatJson(responseEntity.getBody())
                .whenIgnoringPaths("timestamp")
                .isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> postProfileBadRequestSource() {
        return Stream.of(
                Arguments.of("post-request-profile-empty-fields-400.json", "post-response-profile-empty-fields-400.json"),
                Arguments.of("post-request-profile-blank-fields-400.json", "post-response-profile-blank-fields-400.json")
        );
    }

    private static HttpEntity<String> buildHttpEntity(String request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(request, httpHeaders);
    }

}