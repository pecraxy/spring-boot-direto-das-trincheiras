package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.repository.UserData;
import academy.devdojo.repository.UserHardCodedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;


@WebMvcTest(controllers = UserController.class)
@ComponentScan("academy.devdojo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private FileUtils fileUtils;

    @MockitoBean
    private UserData userData;

    @MockitoSpyBean
    private UserHardCodedRepository repository;

    private List<User> userList;

    private final String URL = "/v1/users";

    @BeforeEach
    void init(){
        userList = userUtils.newUserList();
    }

    @Test
    @DisplayName("GET v1/users returns a list with all users when all arguments are null")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenAllArgumentsAreNull() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
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
        var firstName = userList.getFirst().getFirstName();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var response = fileUtils.readSourceFile("users/get-users-firstName-sunless-200.json");
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
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var response = fileUtils.readSourceFile("users/get-users-firstName-notFound-x-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", firstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(4)
    @DisplayName("GET v1/users?lastName=Shadow returns a list with found user when lastName exists")
    void findAll_returnsFoundUser_whenLastNameExists() throws Exception{
        String lastName = userList.getFirst().getLastName();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        String response = fileUtils.readSourceFile("users/get-users-lastName-shadow-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("lastName", lastName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(5)
    @DisplayName("GET v1/users?lastName=not-found returns empty list when lastName is not found")
    void findAll_ReturnsEmptyList_WhenLastNameNotFound() throws Exception {
        String lastName = "not-found";
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        String response = fileUtils.readSourceFile("users/get-users-lastName-notFound-x-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("lastName", lastName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(6)
    @DisplayName("GET v1/users?firstName=Sunless&lastName=Shadow returns a list with found user when firstname and lastName exists")
    void findAll_returnsFoundUser_whenFirstNameAndLastNameExists() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        User expectedUser = userList.getFirst();
        String firstName = expectedUser.getFirstName();
        String lastName = expectedUser.getLastName();
        String response = fileUtils.readSourceFile("users/get-users-firstName-sunless-lastName-shadow-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                    .param("firstName", firstName).param("lastName", lastName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(7)
    @DisplayName("GET v1/users?firstName=not-found&lastName=not-found returns a empty list when firstName and lastName are not found")
    void findAll_returnsEmptyList_whenFirstNameAndLastNameAreNotFound() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(Collections.emptyList());
        String firstName = "not-found";
        String lastName = "not-found";
        String response = fileUtils.readSourceFile("users/get-users-firstName-notFound-lastName-notFound-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .param("firstName", firstName).param("lastName", lastName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(8)
    @DisplayName("GET v1/users?email=shadowslave@example.com returns found user when email exists")
    void findAll_returnsFoundUser_whenEmailExists() throws Exception {
        User expectedUser = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(Collections.singletonList(expectedUser));
        String email = expectedUser.getEmail();
        String response = fileUtils.readSourceFile("users/get-users-email-shadowslave@example.com-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("email", email))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(9)
    @DisplayName("GET v1/users?email=not-found returns empty list when email is not found")
    void findAll_returnsEmptyList_whenEmailIsNotFound() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        String notFound = "not-found";
        String response = fileUtils.readSourceFile("users/get-users-email-notfound-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("email", notFound))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(10)
    @DisplayName("GET v1/users?firstName=Sunless&lastName=Shadow&email=shadowslave@example.com returns foundUser when firstName, lastName and email exists")
    void findAll_returnsFoundUser_WhenFirstNameLastNameAndEmailExists() throws Exception {
        User expectedUser = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(Collections.singletonList(expectedUser));
        String response = fileUtils.readSourceFile("users/get-users-firstname-sunless-lastname-shadow-email-shadowslave@example.com-200.json");
        String firstName = expectedUser.getFirstName();
        String lastName = expectedUser.getLastName();
        String email = expectedUser.getEmail();
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .param("firstName", firstName).param("lastName", lastName).param("email", email))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(11)
    @DisplayName("GET v1/users?firstName=not-found&lastName=not-found&email=not-found returns empty list when firstName, lastName and email are not found")
    void findAll_returnsEmptyList_WhenFirstNameLastNameAndEmailAreNotFound() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(Collections.emptyList());
        String response = fileUtils.readSourceFile("users/get-users-firstName-notFound-lastName-notFound-200.json");
        String firstName = "not-found";
        String lastName = "not-found";
        String email = "not-found";
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .param("firstName", firstName).param("lastName", lastName).param("email", email))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(12)
    @DisplayName("GET v1/users/1 returns user when successful")
    void findById_returnsUserWhenSuccessful() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        Long expectedId = userList.getFirst().getId();
        String response = fileUtils.readSourceFile("users/get-users-by-id-1-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", expectedId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(13)
    @DisplayName("GET v1/users/99 throws ResponseStatusException 404 when user is not found")
    void findById_throwsResponseStatusException_WhenAnimeIsNotFound() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        Long expectedId = 99L;
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", expectedId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found"));
    }

    @Test
    @Order(14)
    @DisplayName("POST v1/users creates an user")
    void save_createsAnUser_WhenSuccessful() throws Exception {
        String request = fileUtils.readSourceFile("users/post-request-user-200.json");
        String response = fileUtils.readSourceFile("users/post-response-user-201.json");

        User userToSave = userUtils.newUserToCreate();
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
    @Order(15)
    @DisplayName("DELETE v1/users/1 deletes an user when user exists")
    void delete_deletesAnUser_whenUserExists() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        Long userIdToDelete = userList.getFirst().getId();
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", userIdToDelete))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @Order(16)
    @DisplayName("DELETE v1/users/1 throws ResponseStatusException 404 when user is not found")
    void delete_throwsResponseStatusException_whenUserIsNotFound() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        Long userIdToDelete = 99L;
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", userIdToDelete))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found"));
    }

    @Test
    @Order(17)
    @DisplayName("PUT v1/users updates an User")
    void update_updatesAnUser_WhenSuccessful() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        String request = fileUtils.readSourceFile("users/put-request-user-200.json");
        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @Order(18)
    @DisplayName("PUT v1/users throws ResponseStatusException 404 when user is not found")
    void update_throwResponseStatusException() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        String request = fileUtils.readSourceFile("users/put-request-user-404.json");
        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found"));
    }

    @Test
    @Order(19)
    @DisplayName("POST v1/users returns bad request when fields are empty")
    void save_returnsBadRequest_WhenFieldsAreEmpty() throws Exception {
        String request = fileUtils.readSourceFile("users/post-request-user-empty-fields-400.json");

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
        var firstNameError = "The field 'firstName' is required";
        var lastNameError = "The field 'lastName' is required";
        var emailError = "The field 'email' is required";

        Assertions.assertThat(resolvedException.getMessage()).contains(firstNameError, lastNameError, emailError);
    }

    @Test
    @Order(20)
    @DisplayName("POST v1/users returns bad request when fields are blank")
    void save_returnsBadRequest_WhenFieldsAreBlank() throws Exception {
        String request = fileUtils.readSourceFile("users/post-request-user-blank-fields-400.json");

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
        var firstNameError = "The field 'firstName' is required";
        var lastNameError = "The field 'lastName' is required";
        var emailError = "The field 'email' is required";

        Assertions.assertThat(resolvedException.getMessage()).contains(firstNameError, lastNameError, emailError);
    }
}