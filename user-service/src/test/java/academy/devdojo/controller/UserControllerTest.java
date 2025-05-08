package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.repository.UserData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
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

    private List<User> userList;

    private final String URL = "/v1/users";

    @BeforeEach
    void init(){
        userList = userUtils.newUserList();
    }

    @Test
    @DisplayName("findAll returns a list with all users when all arguments are null")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenAllArgumentsAreNull() throws Exception {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var response = fileUtils.readSourceFile("user/get-users-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findAll returns a list with found user when firstName exists")
    @Order(2)
    void findAll_returnsFoundUser_whenFirstNameExists() throws Exception {
        var firstName = userList.getFirst().getFirstName();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var response = fileUtils.readSourceFile("user/get-users-firstName-sunless-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", firstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(3)
    @DisplayName("findAll returns empty list when firstName is not found")
    void findAll_returnsEmptyList_whenFirstNameIsNotFound() throws Exception {
        var firstName = "not-found";
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var response = fileUtils.readSourceFile("user/get-users-firstName-notFound-x-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", firstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(4)
    @DisplayName("findAll returns a list with found user when lastName exists")
    void findAll_returnsFoundUser_whenLastNameExists() throws Exception{
        var lastName = userList.getFirst().getLastName();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var response = fileUtils.readSourceFile("user/get-users-lastName-shadow-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("lastName", lastName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(5)
    @DisplayName("findAll returns empty list when lastName is not found")
    void findAll_ReturnsEmptyList_WhenLastNameNotFound() throws Exception {
        var lastName = "not-found";
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var response = fileUtils.readSourceFile("user/get-users-lastName-notFound-x-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("lastName", lastName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(6)
    @DisplayName("findAll returns a list with found user when firstname and lastName exists")
    void findAll_returnsFoundUser_whenFirstNameAndLastNameExists() {
        var expectedUser = userList.getFirst();
        var firstName = expectedUser.getFirstName();
        var lastName = expectedUser.getLastName();
        var response = fileUtils.readSourceFile("")
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                .param("firstName", firstName).param("lastName", lastName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect()
    }
}