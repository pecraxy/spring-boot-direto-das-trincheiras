package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.exception.EmailAlreadyExistsException;
import academy.devdojo.repository.ProfileRepository;
import academy.devdojo.repository.UserProfileRepository;
import academy.devdojo.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


@WebMvcTest(controllers = UserController.class)
@ComponentScan({"academy.devdojo"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private FileUtils fileUtils;

    @MockitoBean
    private UserRepository repository;

    @MockitoBean
    private ProfileRepository profileRepository;

    @MockitoBean
    private UserProfileRepository userProfileRepository;

    private List<User> userList;

    private final String URL = "/v1/users";

    @BeforeEach
    void init() {
        userList = userUtils.newUserList();
    }

    @Test
    @DisplayName("GET v1/users returns a list with all users when all arguments are null")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenAllArgumentsAreNull() throws Exception {
        BDDMockito.when(repository.findAll()).thenReturn(userList);
        var response = fileUtils.readSourceFile("users/get-users-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }


    @Test
    @DisplayName("GET v1/users?firstName=Sunless returns a list with found user when firstName exists")
    @Order(2)
    void findAll_returnsFoundUser_whenFirstNameExists() throws Exception {
        var firstName = "Sunless";
        var response = fileUtils.readSourceFile("users/get-users-firstName-sunless-200.json");
        var sunless = userList.stream().filter(user -> user.getFirstName().equalsIgnoreCase(firstName)).findFirst().orElse(null);
        BDDMockito.when(repository.findByFirstNameEqualsIgnoreCase(firstName)).thenReturn(Collections.singletonList(sunless));
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", firstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(3)
    @DisplayName("GET v1/users?firstName=not-found returns empty list when firstName is not found")
    void findAll_returnsEmptyList_whenFirstNameIsNotFound() throws Exception {
        var firstName = "not-found";
        var response = fileUtils.readSourceFile("users/get-users-firstName-notFound-x-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", firstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(4)
    @DisplayName("GET v1/users/1 returns user when successful")
    void findById_returnsUserWhenSuccessful() throws Exception {
        Long expectedId = 1L;
        var foundUser = userList.stream().filter(user -> user.getId().equals(expectedId)).findFirst();

        BDDMockito.when(repository.findById(expectedId)).thenReturn(foundUser);
        String response = fileUtils.readSourceFile("users/get-users-by-id-1-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", expectedId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(5)
    @DisplayName("GET v1/users/99 throws NotFound 404 when user is not found")
    void findById_throwsNotFound_WhenAnimeIsNotFound() throws Exception {
        var response = fileUtils.readSourceFile("users/get-users-by-id-99-404.json");
        Long expectedId = 99L;
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", expectedId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(6)
    @DisplayName("POST v1/users creates an user")
    void save_createsAnUser_WhenSuccessful() throws Exception {
        String request = fileUtils.readSourceFile("users/post-request-user-200.json");
        String response = fileUtils.readSourceFile("users/post-response-user-201.json");

        User userToSave = userUtils.newUserToCreate().withId(99L);
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(userToSave);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(7)
    @DisplayName("POST v1/users throws EmailAlreadyExistsException when email is already used")
    void save_ThrowsEmailAlreadyExistsException_WhenEmailAlreadyUsed() throws Exception {
        String request = fileUtils.readSourceFile("users/post-request-user-email-already-exists-400.json");
        String response = fileUtils.readSourceFile("users/post-response-user-email-already-exists-400.json");
        String emailAlreadyUsed = userUtils.newUserToCreateWithEmailAlreadyUsed().getEmail();
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenThrow(new EmailAlreadyExistsException("Email %s already exists".formatted(emailAlreadyUsed)));
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(7)
    @DisplayName("DELETE v1/users/1 deletes an user when user exists")
    void delete_deletesAnUser_whenUserExists() throws Exception {
        Long id = userList.getFirst().getId();
        var foundUser = userList.stream().filter(user -> user.getId().equals(id)).findFirst();
        BDDMockito.when(repository.findById(id)).thenReturn(foundUser);
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @Order(8)
    @DisplayName("DELETE v1/users/1 throws NotFound 404 when user is not found")
    void delete_throwsNotFound_whenUserIsNotFound() throws Exception {
        Long userIdToDelete = 99L;
        var response = fileUtils.readSourceFile("users/delete-user-by-id-99-404.json");
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", userIdToDelete))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(9)
    @DisplayName("PUT v1/users updates an User")
    void update_updatesAnUser_WhenSuccessful() throws Exception {
        Long id = 1L;
        var foundUser = userList.stream().filter(user -> user.getId().equals(id)).findFirst();
        BDDMockito.when(repository.findById(id)).thenReturn(foundUser);
        String request = fileUtils.readSourceFile("users/put-request-user-200.json");
        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @Order(9)
    @DisplayName("PUT v1/users throws EmailAlreadyExistsException when e-mail is already used")
    void update_ThrowsEmailAlreadyExistsException_WhenEmailAlreadyUsed() throws Exception {
        Long id = 1L;
        String emailAlreadyUsed = userList.getLast().getEmail();
        var foundUser = userList.stream().filter(user -> user.getId().equals(id)).findFirst();
        BDDMockito.when(repository.findById(id)).thenReturn(foundUser);
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenThrow(new EmailAlreadyExistsException("Email %s already exists".formatted(emailAlreadyUsed)));
        String request = fileUtils.readSourceFile("users/put-request-user-email-already-exists-400.json");
        String response = fileUtils.readSourceFile("users/put-response-user-email-already-exists-400.json");

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(10)
    @DisplayName("PUT v1/users throws NotFound 404 when user is not found")
    void update_throwNotFound() throws Exception {

        String request = fileUtils.readSourceFile("users/put-request-user-404.json");
        var response = fileUtils.readSourceFile("users/put-user-by-id-99-404.json");
        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @ParameterizedTest
    @MethodSource("postUserBadRequestSource")
    @Order(11)
    @DisplayName("POST v1/users returns bad request when fields are empty or invalid")
    void save_returnsBadRequest_WhenFieldsAreEmptyOrInvalid(String fileName, List<String> errors) throws Exception {
        String request = fileUtils.readSourceFile("users/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();


        Exception resolvedException = mvcResult.getResolvedException();
        Assertions.assertThat(resolvedException).isNotNull();
        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    @ParameterizedTest
    @MethodSource("putUserBadRequestSource")
    @Order(12)
    @DisplayName("PUT v1/users returns bad request when fields are empty or invalid")
    void update_returnsBadRequest_WhenFieldsAreEmptyOrInvalid(String fileName, List<String> errors) throws Exception {
        String request = fileUtils.readSourceFile("users/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();


        Exception resolvedException = mvcResult.getResolvedException();
        Assertions.assertThat(resolvedException).isNotNull();
        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    private static Stream<Arguments> postUserBadRequestSource() {
        var allRequiredErrors = allRequiredErrors();
        var invalidEmailErrors = invalidEmailErrors();
        return Stream.of(
                Arguments.of("post-request-user-empty-fields-400.json", allRequiredErrors),
                Arguments.of("post-request-user-blank-fields-400.json", allRequiredErrors),
                Arguments.of("post-request-user-invalid-email-400.json", invalidEmailErrors)
        );
    }

    private static Stream<Arguments> putUserBadRequestSource() {
        var allRequiredErrors = allRequiredErrors();
        allRequiredErrors.add("The user id cannot be null");
        var invalidEmailErrors = invalidEmailErrors();
        var idMustBePositiveError = Collections.singletonList("The user id must be a positive number higher than one");
        return Stream.of(
                Arguments.of("put-request-user-empty-fields-400.json", allRequiredErrors),
                Arguments.of("put-request-user-blank-fields-400.json", allRequiredErrors),
                Arguments.of("put-request-user-invalid-email-400.json", invalidEmailErrors),
                Arguments.of("put-request-user-id-negative-400.json", idMustBePositiveError)

        );

    }

    private static List<String> allRequiredErrors() {
        var firstNameRequiredError = "The field 'firstName' is required";
        var lastNameRequiredError = "The field 'lastName' is required";
        var emailRequiredError = "The field 'email' is required";
        return new ArrayList<>(List.of(firstNameRequiredError, lastNameRequiredError, emailRequiredError));
    }

    private static List<String> invalidEmailErrors() {
        var emailInvalidError = "Email is not valid";
        return List.of(emailInvalidError);
    }

//    private static List<String> idErrors


}