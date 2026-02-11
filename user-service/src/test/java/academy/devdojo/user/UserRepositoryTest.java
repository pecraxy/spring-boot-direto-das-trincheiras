package academy.devdojo.user;

import academy.devdojo.commons.UserUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserUtils.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserUtils userUtils;

    @Test
    @DisplayName("save creates an user")
    @Order(1)
    void save_CreatesUser_WhenSuccessful(){
        var userToSave = userUtils.newUserToCreate();

        var savedUser = repository.save(userToSave);

        Assertions.assertThat(userToSave).hasNoNullFieldsOrProperties();
        Assertions.assertThat(savedUser.getId()).isEqualTo(1);
    }

    @Test
    @Order(2)
    @DisplayName("findAll returns all users when successful")
    @Sql("/sql/init_one_user.sql")
    void findAll_returnsAllUsers_whenSuccessful() {
        var users = repository.findAll();
        Assertions.assertThat(users).isNotEmpty();
    }
}