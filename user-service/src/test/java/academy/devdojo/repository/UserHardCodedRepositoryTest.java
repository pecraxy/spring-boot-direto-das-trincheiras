package academy.devdojo.repository;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
class UserHardCodedRepositoryTest {

    private List<User> userList;

    @InjectMocks
    private UserHardCodedRepository repository;

    @InjectMocks
    private UserUtils userUtils;

    @Mock
    private UserData userData;

    @BeforeEach
    void init() {
        userList = userUtils.newUserList();
    }

    @Test
    @DisplayName("findAll returns a list with all users when successful")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenSuccessful() {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findAll();
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSameElementsAs(userList)
                .hasSize(userList.size());
    }

    @Test
    @DisplayName("findByFirstName returns list with found object when name is found")
    @Order(2)
    void findByFirstName_ReturnsFoundUsersInList_WhenFirstNameIsFound() {
        var expectedProducer = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findByFirstName(expectedProducer.getFirstName());

        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .contains(expectedProducer)
                .hasSize(1);
    }

    @Test
    @DisplayName("findByFirstName returns empty list when name is null")
    @Order(3)
    void findByFirstName_ReturnsEmptyList_WhenNameIsNull() {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findByFirstName(null);

        Assertions.assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns an User with given id")
    @Order(4)
    void findById_ReturnsUser_WhenSuccessful() {
        var expectedUser = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var foundUser = repository.findById(expectedUser.getId());
        log.info(foundUser);
        Assertions.assertThat(foundUser)
                .isNotNull()
                .isPresent()
                .contains(expectedUser);
    }

    @Test
    @DisplayName("save create an User")
    @Order(5)
    void save_CreateAnUser_WhenSuccessful() {
        var userToSave = userUtils.newUserToCreate();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        User savedUser = repository.save(userToSave);
        log.info(savedUser);

        Assertions.assertThat(savedUser)
                .isNotNull()
                .isEqualTo(userToSave)
                .hasNoNullFieldsOrProperties();

        Optional<User> userOptional = repository.findById(userToSave.getId());
        Assertions.assertThat(userOptional)
                .isNotNull()
                .isPresent()
                .contains(userToSave);
    }

    @Test
    @DisplayName("delete removes an user")
    @Order(6)
    void delete_removesAnUser_WhenSuccessful() {
        var userToDelete = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        repository.delete(userToDelete);
        Assertions.assertThatNoException()
                .isThrownBy(() -> repository.delete(userToDelete));

        Assertions.assertThat(this.userList)
                .isNotEmpty()
                .doesNotContain(userToDelete);

        List<User> users = repository.findAll();

        Assertions.assertThat(users)
                .isNotEmpty()
                .doesNotContain(userToDelete);
    }

    @Test
    @DisplayName("update update an user")
    @Order(7)
    void update_UpdateAnUser_WhenSuccessful() {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        var userToUpdate = userList.getFirst();
        userToUpdate.setFirstName("Sunny");
        repository.update(userToUpdate);

        Assertions.assertThat(this.userList)
                .contains(userToUpdate);

        Optional<User> userUpdateOptional = repository.findById(userToUpdate.getId());
        Assertions.assertThat(userUpdateOptional)
                .isPresent();

        Assertions.assertThat(userUpdateOptional.get().getFirstName())
                .isEqualTo(userToUpdate.getFirstName());
    }
}