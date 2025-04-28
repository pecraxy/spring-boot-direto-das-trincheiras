package academy.devdojo.service;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.repository.UserHardCodedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @InjectMocks
    UserService service;

    @InjectMocks
    UserUtils userUtils;

    @Mock
    UserHardCodedRepository repository;

    private List<User> userList;

    @BeforeEach
    void init(){
        userList = userUtils.newUserList();
    }

    @Test
    @Order(1)
    @DisplayName("findAll returns all users when arguments are null")
    void findAll_returnsAllUsers_whenArgumentsAreNull() {
        BDDMockito.when(repository.findAll()).thenReturn(userList);
        var users = service.findAll(null, null, null);
        Assertions.assertThat(users)
                .isNotEmpty()
                .isNotNull()
                .hasSize(userList.size())
                .hasSameElementsAs(userList);
    }

    @Test
    @Order(2)
    @DisplayName("findAll returns a list with found user when firstName exists")
    void findAll_returnsFoundUser_whenFirstNameExists() {
        var expectedUser = userList.getFirst();
        BDDMockito.when(repository.findByName(expectedUser.getFirstName(), null)).thenReturn(Collections.singletonList(expectedUser));
        var users = service.findAll(expectedUser.getFirstName(), null, null);
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedUser);
    }

    @Test
    @Order(3)
    @DisplayName("findAll returns empty list when firstName is not found")
    void findAll_returnsEmptyList_whenFirstNameIsNotFound() {
        var firstName = "not-found";
        BDDMockito.when(repository.findByName(firstName, null)).thenReturn(Collections.emptyList());
        var users = service.findAll(firstName, null, null);
        Assertions.assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("findAll returns a list with found user when lastName exists")
    void findAll_returnsFoundUser_whenLastNameExists() {
        var expectedUser = userList.getFirst();
        BDDMockito.when(repository.findByName(null, expectedUser.getLastName())).thenReturn(Collections.singletonList(expectedUser));
        var users = service.findAll(null, expectedUser.getLastName(), null);
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedUser);
    }

    @Test
    @Order(4)
    @DisplayName("findAll returns empty list when lastName is not found")
    void findAll_returnsEmptyList_whenLastNameIsNotFound() {
        var lastName = "not-found";
        BDDMockito.when(repository.findByName(null, lastName)).thenReturn(Collections.emptyList());
        var users = service.findAll(null, lastName, null);
        Assertions.assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("findAll returns a list with found user when firstname and lastName exists")
    void findAll_returnsFoundUser_whenFirstNameAndLastNameExists() {
        var expectedUser = userList.getFirst();
        BDDMockito.when(repository.findByName(expectedUser.getFirstName(), expectedUser.getLastName())).thenReturn(Collections.singletonList(expectedUser));
        var users = service.findAll(expectedUser.getFirstName(), expectedUser.getLastName(), null);
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedUser);
    }

    @Test
    @Order(5)
    @DisplayName("findAll returns empty list when firstName and lastName are not found")
    void findAll_returnsEmptyList_whenFirstNameAndLastNameAreNotFound() {
        var firstName = "not-found";
        var lastName = "not-found";
        BDDMockito.when(repository.findByName(firstName, lastName)).thenReturn(Collections.emptyList());
        var users = service.findAll(firstName, lastName, null);
        Assertions.assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @Order(6)
    @DisplayName("findAll returns a list with found user when email exists")
    void findAll_returnsFoundUser_whenEmailExists() {
        var expectedUser = userList.getFirst();
        BDDMockito.when(repository.findByEmail(expectedUser.getEmail())).thenReturn(Collections.singletonList(expectedUser));
        var users = service.findAll(null, null, expectedUser.getEmail());
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedUser);
    }

    @Test
    @Order(7)
    @DisplayName("findAll returns empty list when email is not found")
    void findAll_returnsEmptyList_whenEmailIsNotFound() {
        var email = "notfound@notfound.com";
        BDDMockito.when(repository.findByEmail(email)).thenReturn(Collections.emptyList());
        var users = service.findAll(null, null, email);
        Assertions.assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @Order(8)
    @DisplayName("findById returns user when successful")
    void findById_returnsUser_whenSuccessful() {
        var expectedUser = userList.getFirst();
        var id = expectedUser.getId();
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(expectedUser));
        User foundUser = service.findByIdOrElseThrowResponseStatusException(id);
        Assertions.assertThat(foundUser)
                .isNotNull()
                .isEqualTo(expectedUser);
    }

    @Test
    @Order(9)
    @DisplayName("findById throws ResponseStatusException when user is not found")
    void findById_ThrowsResponseStatusException_WhenUserIsNotFound() {
        var expectedUser = userList.getFirst();
        var id = expectedUser.getId();
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findByIdOrElseThrowResponseStatusException(id))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Order(10)
    @DisplayName("save creates an user")
    void save_createsAnUser_WhenSuccessful() {
        var userToCreate = userUtils.newUserToCreate();
        BDDMockito.when(repository.save(userToCreate)).thenReturn(userToCreate);
        User savedUser = service.save(userToCreate);
        Assertions.assertThat(savedUser)
                .isNotNull()
                .isEqualTo(userToCreate)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @Order(11)
    @DisplayName("delete removes an user")
    void delete_removesAnUser_WhenSuccessful() {
        var expectedUserToDelete = userList.getFirst();
        BDDMockito.when(repository.findById(expectedUserToDelete.getId())).thenReturn(Optional.of(expectedUserToDelete));
        BDDMockito.doNothing().when(repository).delete(expectedUserToDelete);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.delete(expectedUserToDelete.getId()));
    }

    @Test
    @Order(12)
    @DisplayName("delete throws ResponseStatusException when user is not found")
    void delete_ThrowsResponseStatusException_WhenUserNotFound() {
        var expectedUserToDelete = userList.getFirst();

        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(expectedUserToDelete.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Order(13)
    @DisplayName("update updates an User")
    void update_UpdateUser_WhenSuccessful() {
        var expectedUserToUpdate = userList.getFirst();
        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(expectedUserToUpdate));
        expectedUserToUpdate.setFirstName("Sunny");
        BDDMockito.doNothing().when(repository).update(expectedUserToUpdate);
        service.update(expectedUserToUpdate);
        Assertions.assertThatNoException()
                .isThrownBy(() -> service.update(expectedUserToUpdate));
    }

    @Test
    @Order(14)
    @DisplayName("update throws ResponseStatusException when user is not found")
    void update_ThrowsResponseStatusException_WhenUserNotFound() {
        var expectedUserToUpdate = userList.getFirst();
        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(expectedUserToUpdate))
                .isInstanceOf(ResponseStatusException.class);
    }

}