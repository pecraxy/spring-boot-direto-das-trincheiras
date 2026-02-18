package academy.devdojo.service;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.exception.EmailAlreadyExistsException;
import academy.devdojo.repository.UserRepository;
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
    UserRepository repository;


    private List<User> userList;

    @BeforeEach
    void init() {
        userList = userUtils.newUserList();
    }

    @Test
    @Order(1)
    @DisplayName("findAll returns all users when arguments are null")
    void findAll_returnsAllUsers_whenArgumentsAreNull() {
        BDDMockito.when(repository.findAll()).thenReturn(userList);
        var users = service.findAll(null);
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
        BDDMockito.when(repository.findByFirstNameEqualsIgnoreCase(expectedUser.getFirstName())).thenReturn(Collections.singletonList(expectedUser));
        var users = service.findAll(expectedUser.getFirstName());
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
        BDDMockito.when(repository.findByFirstNameEqualsIgnoreCase(firstName)).thenReturn(Collections.emptyList());
        var users = service.findAll(firstName);
        Assertions.assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("findById returns user when successful")
    void findById_returnsUser_whenSuccessful() {
        var expectedUser = userList.getFirst();
        var id = expectedUser.getId();
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(expectedUser));
        User foundUser = service.findByIdOrElseThrowNotFoundException(id);
        Assertions.assertThat(foundUser)
                .isNotNull()
                .isEqualTo(expectedUser);
    }

    @Test
    @Order(5)
    @DisplayName("findById throws ResponseStatusException when user is not found")
    void findById_ThrowsResponseStatusException_WhenUserIsNotFound() {
        var expectedUser = userList.getFirst();
        var id = expectedUser.getId();
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findByIdOrElseThrowNotFoundException(id))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Order(6)
    @DisplayName("save creates an user")
    void save_createsAnUser_WhenSuccessful() {
        var userToCreate = userUtils.newUserToCreate().withId(4L);

        BDDMockito.when(repository.save(userToCreate)).thenReturn(userToCreate);
        BDDMockito.when(repository.findByEmail(userToCreate.getEmail())).thenReturn(Optional.empty());

        User savedUser = service.save(userToCreate);

        Assertions.assertThat(savedUser)
                .isNotNull()
                .isEqualTo(userToCreate)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @Order(7)
    @DisplayName("save throws EmailAlreadyExistsException when Email Already Exists")
    void save_ThrowsEmailAlreadyExistsException_WhenEmailAlreadyUsed() {
        var savedUser = userList.getLast();
        var userToCreate = userUtils.newUserToCreate().withEmail(savedUser.getEmail());

        BDDMockito.when(repository.findByEmail(userToCreate.getEmail())).thenReturn(Optional.of(savedUser));

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(userToCreate))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    @Order(8)
    @DisplayName("delete removes an user")
    void delete_removesAnUser_WhenSuccessful() {
        var expectedUserToDelete = userList.getFirst();
        BDDMockito.when(repository.findById(expectedUserToDelete.getId())).thenReturn(Optional.of(expectedUserToDelete));
        BDDMockito.doNothing().when(repository).delete(expectedUserToDelete);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.delete(expectedUserToDelete.getId()));
    }

    @Test
    @Order(9)
    @DisplayName("delete throws ResponseStatusException when user is not found")
    void delete_ThrowsResponseStatusException_WhenUserNotFound() {
        var expectedUserToDelete = userList.getFirst();

        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(expectedUserToDelete.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Order(10)
    @DisplayName("update updates an User")
    void update_UpdateUser_WhenSuccessful() {
        var expectedUserToUpdate = userList.getFirst().withFirstName("Sunny").withEmail("sunny@example.com");
        var email = expectedUserToUpdate.getEmail();
        var id = expectedUserToUpdate.getId();

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(expectedUserToUpdate));
        BDDMockito.when(repository.findByEmailAndIdNot(email, id)).thenReturn(Optional.empty());
        BDDMockito.when(repository.save(expectedUserToUpdate)).thenReturn(expectedUserToUpdate);

        service.update(expectedUserToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(expectedUserToUpdate));
    }

    @Test
    @Order(11)
    @DisplayName("update throws EmailAlreadyExistsException when email updated already used")
    void update_ThrowsEmailAlreadyExistsException_whenEmailUpdatedAlreadyUsed() {
        var savedUser = userList.getLast();
        var expectedUserToUpdate = userList.getFirst().withEmail(savedUser.getEmail());
        var email = expectedUserToUpdate.getEmail();
        var id = expectedUserToUpdate.getId();

        BDDMockito.when(repository.findById(expectedUserToUpdate.getId())).thenReturn(Optional.of(expectedUserToUpdate));

        String message = "Email %s already used".formatted(expectedUserToUpdate.getEmail());

        BDDMockito.when(repository.findByEmailAndIdNot(email, id)).thenThrow(new EmailAlreadyExistsException(message));

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(expectedUserToUpdate))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    @Order(12)
    @DisplayName("update throws ResponseStatusException when user is not found")
    void update_ThrowsResponseStatusException_WhenUserNotFound() {
        var expectedUserToUpdate = userList.getFirst();
        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(expectedUserToUpdate))
                .isInstanceOf(ResponseStatusException.class);
    }

}