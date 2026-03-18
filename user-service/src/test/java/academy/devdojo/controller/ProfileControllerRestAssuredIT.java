package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.config.IntegrationTestConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileControllerRestAssuredIT extends IntegrationTestConfig {

    private static final String URL = "/v1/profiles";

    @LocalServerPort
    private int port;

    @Autowired
    private FileUtils fileUtils;

    @BeforeEach
    void setUrl() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = this.port;
    }

    @Test
    @DisplayName("GET /v1/profiles returns a list with all profiles")
    @Sql(value = "/sql/init_two_profiles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_profiles.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenSuccessful() {
        var response = fileUtils.readSourceFile("/profiles/get-profiles-200.json");
        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET /v1/profiles returns empty list when nothing is not found")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNothingIsNotFound() {
        var response = fileUtils.readSourceFile("/profiles/get-profiles-empty-list-200.json");
        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @Order(3)
    @DisplayName("POST v1/profiles save a Profile when successful")
    void save_SaveAProfile_WhenSuccessful() {
        var request = fileUtils.readSourceFile("profiles/post-request-profiles-201.json");
        var expectedResponse = fileUtils.readSourceFile("profiles/post-response-profiles-201.json");

        String response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("id")
                .asNumber()
                .isPositive();
        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("postProfileBadRequestSource")
    @Order(4)
    @DisplayName("POST v1/profiles returns bad request when fields are empty or invalid")
    void save_ReturnsBadRequest_WhenFieldsAreEmptyOrInvalid(String requestFile, String responseFile) throws Exception {
        var request = fileUtils.readSourceFile("/profiles/%s".formatted(requestFile));
        var expectedResponse = fileUtils.readSourceFile("/profiles/%s".formatted(responseFile));

        String response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> postProfileBadRequestSource() {
        return Stream.of(
                Arguments.of("post-request-profile-empty-fields-400.json", "post-response-profile-empty-fields-400.json"),
                Arguments.of("post-request-profile-blank-fields-400.json", "post-response-profile-blank-fields-400.json")
        );
    }


}