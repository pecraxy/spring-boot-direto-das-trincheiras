package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.config.IntegrationTestConfig;
import academy.devdojo.domain.User;
import academy.devdojo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
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

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerRestAssuredIT extends IntegrationTestConfig {

    private final String URL = "/v1/users";

    @LocalServerPort
    private int port;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUrl() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = this.port;
    }


    @Test
    @DisplayName("GET v1/users returns a list with all users when all arguments are null")
    @Order(1)
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnsAllUsers_WhenAllArgumentsAreNull() {
        var expectedResponse = fileUtils.readSourceFile("users/get-users-200.json");

        String response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .and(users -> {
                    users.node("[0].id").asNumber().isPositive();
                    users.node("[1].id").asNumber().isPositive();
                    users.node("[2].id").asNumber().isPositive();
                });
        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/users?firstName=Sunless returns a list with found user when firstName exists")
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(2)
    void findAll_returnsFoundUser_whenFirstNameExists() {
        var firstName = "Sunless";
        var expectedResponse = fileUtils.readSourceFile("users/get-users-firstName-sunless-200.json");
        String response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .queryParam("firstName", firstName)
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();
        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(expectedResponse);
    }

    @Test
    @Order(3)
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("GET v1/users?firstName=not-found returns empty list when firstName is not found")
    void findAll_returnsEmptyList_whenFirstNameIsNotFound() {
        var firstName = "not-found";
        var expectedResponse = fileUtils.readSourceFile("users/get-users-firstName-notFound-x-200.json");
        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .queryParam("firstName", firstName)
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();
    }

    @Test
    @Order(4)
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("GET v1/users/1 returns user when successful")
    void findById_returnsUserWhenSuccessful() {
        var users = userRepository.findByFirstNameEqualsIgnoreCase("Sunless");
        Assertions.assertThat(users).hasSize(1);
        String expectedResponse = fileUtils.readSourceFile("users/get-users-by-id-1-200.json");

        String response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", users.getFirst().getId())
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();
        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedResponse);
    }

    @Test
    @Order(5)
    @DisplayName("GET v1/users/99 throws NotFound 404 when user is not found")
    void findById_throwsNotFound_WhenAnimeIsNotFound() {
        Long expectedId = 99L;

        var expectedResponse = fileUtils.readSourceFile("users/get-users-by-id-99-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", expectedId)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();
    }

    @Test
    @Order(6)
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("POST v1/users creates an user")
    void save_createsAnUser_WhenSuccessful() {
        String request = fileUtils.readSourceFile("users/post-request-user-200.json");
        String expectedResponse = fileUtils.readSourceFile("users/post-response-user-201.json");

        String response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .body(request)
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

    @Test
    @Order(7)
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("POST v1/users throws EmailAlreadyExistsException when email is already used")
    void save_ThrowsEmailAlreadyExistsException_WhenEmailAlreadyUsed() {
        String request = fileUtils.readSourceFile("users/post-request-user-email-already-exists-400.json");
        String expectedResponse = fileUtils.readSourceFile("users/post-response-user-email-already-exists-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .body(request)
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();
    }

    @Test
    @Order(8)
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("DELETE v1/users/1 deletes an user when user exists")
    void delete_deletesAnUser_whenUserExists() {
        List<User> users = userRepository.findByFirstNameEqualsIgnoreCase("Sunless");
        Assertions.assertThat(users).hasSize(1);

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", users.getFirst().getId())
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();
    }

    @Test
    @Order(9)
    @DisplayName("DELETE v1/users/99 throws NotFound 404 when user is not found")
    void delete_throwsNotFound_whenUserIsNotFound() {
        Long id = 99L;
        var expectedResponse = fileUtils.readSourceFile("users/delete-user-by-id-99-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", id)
                .delete(URL + "/{id}")
                .then()
                .body(Matchers.equalTo(expectedResponse))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    @Order(10)
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("PUT v1/users updates an User")
    void update_updatesAnUser_WhenSuccessful() {
        String request = fileUtils.readSourceFile("users/put-request-user-200.json");

        var users = userRepository.findByFirstNameEqualsIgnoreCase("Sunless");
        Assertions.assertThat(users).hasSize(1);

        request = request.replace("1", users.getFirst().getId().toString());

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();
    }

    @Test
    @Order(11)
    @Sql(value = "/sql/users/init_three_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("PUT v1/users throws EmailAlreadyExistsException when e-mail is already used")
    void update_ThrowsEmailAlreadyExistsException_WhenEmailAlreadyUsed() {
        String request = fileUtils.readSourceFile("users/put-request-user-email-already-exists-400.json");
        String expectedResponse = fileUtils.readSourceFile("users/put-response-user-email-already-exists-400.json");

        var users = userRepository.findByFirstNameEqualsIgnoreCase("Sunless");
        Assertions.assertThat(users).hasSize(1);

        request = request.replace("1", users.getFirst().getId().toString());

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();
    }

    @Test
    @Order(12)
    @DisplayName("PUT v1/users throws NotFound 404 when user is not found")
    void update_throwNotFound() {

        String request = fileUtils.readSourceFile("users/put-request-user-404.json");
        var expectedResponse = fileUtils.readSourceFile("users/put-user-by-id-99-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();
    }

    @ParameterizedTest
    @MethodSource("postUserBadRequestSource")
    @Order(13)
    @DisplayName("POST v1/users returns bad request when fields are empty or invalid")
    void save_returnsBadRequest_WhenFieldsAreEmptyOrInvalid(String requestFile, String responseFile) {
        String request = fileUtils.readSourceFile("users/%s".formatted(requestFile));
        String expectedResponse = fileUtils.readSourceFile("users/%s".formatted(responseFile));
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

    @ParameterizedTest
    @MethodSource("putUserBadRequestSource")
    @Order(14)
    @DisplayName("PUT v1/users returns bad request when fields are empty or invalid")
    void update_returnsBadRequest_WhenFieldsAreEmptyOrInvalid(String requestFile, String responseFile) {
        String request = fileUtils.readSourceFile("users/%s".formatted(requestFile));
        String expectedResponse = fileUtils.readSourceFile("users/%s".formatted(responseFile));
        String response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();
        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);

    }

    private static Stream<Arguments> postUserBadRequestSource() {

        return Stream.of(
                Arguments.of("post-request-user-empty-fields-400.json", "post-response-user-empty-fields-400.json"),
                Arguments.of("post-request-user-blank-fields-400.json", "post-response-user-blank-fields-400.json"),
                Arguments.of("post-request-user-invalid-email-400.json", "post-response-user-invalid-email-400.json")
        );
    }

    private static Stream<Arguments> putUserBadRequestSource() {

        return Stream.of(
                Arguments.of("put-request-user-empty-fields-400.json", "put-response-user-empty-fields-400.json"),
                Arguments.of("put-request-user-blank-fields-400.json", "put-response-user-blank-fields-400.json"),
                Arguments.of("put-request-user-invalid-email-400.json", "put-response-user-invalid-email-400.json"),
                Arguments.of("put-request-user-id-negative-400.json", "put-response-user-id-negative-400.json")

        );

    }


}