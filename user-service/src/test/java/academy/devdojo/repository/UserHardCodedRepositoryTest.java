package academy.devdojo.repository;

import academy.devdojo.domain.User;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
class UserHardCodedRepositoryTest {

    private List<User> userList;

    @InjectMocks
    private UserHardCodedRepository repository;

    @Mock
    private UserData userData;

    @BeforeEach
    void init(){
        User user1 = User.builder().id(1L).firstName("Sunless").lastName("Shadow").email("marcelosemdente@example.com").build();
        User user2 = User.builder().id(2L).firstName("Kai").lastName("Nightingale").email("williamsuane@example.com").build();
        User user3 = User.builder().id(3L).firstName("Nephis").lastName("Anvil").email("rezendeevil@example.com").build();
        userList = new ArrayList<>(List.of(user1, user2, user3));
    }

    @Test
    @DisplayName("findAll returns a list with all users when successful")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenSuccessful() {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findAll();
        log.info(users);
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSameElementsAs(userList)
                .hasSize(userList.size());
    }

    @Test
    @DisplayName("findByName returns list with found object when both name are found")
    @Order(2)
    void findByName_ReturnsFoundUsersInList_WhenBothNamesFound() {
        var expectedProducer = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findByName(expectedProducer.getFirstName(), expectedProducer.getLastName());
        log.info(users);
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .contains(expectedProducer)
                .hasSize(1);
    }

    @Test
    @DisplayName("findByName returns list with found object when only lastName is null")
    @Order(3)
    void findByName_ReturnsFoundUsersInList_WhenOnlyLastNameIsNull() {
        var expectedProducer = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findByName(null, expectedProducer.getLastName());
        log.info(users);
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .contains(expectedProducer)
                .hasSize(1);
    }

    @Test
    @DisplayName("findByName returns list with found object when only firstName is null")
    @Order(4)
    void findByName_ReturnsFoundUsersInList_WhenOnlyFirstNameIsNull() {
        var expectedProducer = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findByName(expectedProducer.getFirstName(), null);
        log.info(users);
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .contains(expectedProducer)
                .hasSize(1);
    }

    @Test
    @DisplayName("findByName returns empty list when both names are null")
    @Order(5)
    void findByName_ReturnsEmptyList_WhenBothNamesAreNull() {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findByName(null, null);
        log.info(users);
        Assertions.assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findByEmail returns found object when email exists")
    @Order(6)
    void findByEmail_ReturnsUser_WhenEmailExists() {
        var expectedProducer = userList.getFirst();
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findByEmail(expectedProducer.getEmail());
        log.info(users);
        Assertions.assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .contains(expectedProducer)
                .hasSize(1);
    }

    @Test
    @DisplayName("findByEmail returns empty list when email is not found ")
    @Order(7)
    void findByEmail_ReturnsEmptyList_WhenEmailIsNull() {
        BDDMockito.when(userData.getUserList()).thenReturn(userList);
        List<User> users = repository.findByEmail(null);
        log.info(users);
        Assertions.assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns an User with given id")
    @Order(8)
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
}