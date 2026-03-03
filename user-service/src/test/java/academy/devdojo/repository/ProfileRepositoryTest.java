package academy.devdojo.repository;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.config.IntegrationTestConfig;
import academy.devdojo.domain.Profile;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({ProfileUtils.class})
@Slf4j
class ProfileRepositoryTest extends IntegrationTestConfig {

    @Autowired
    private ProfileRepository repository;

    @Autowired
    private ProfileUtils profileUtils;

    @Test
    @Order(1)
    @DisplayName("save Creates a Profile When Successful")
    void save_CreatesAProfile_WhenSuccessful() {
        Profile profileToCreate = profileUtils.newProfileToSave();
        Profile savedUser = repository.save(profileToCreate);
        Assertions.assertThat(savedUser)
                .isNotNull()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @Order(2)
    @DisplayName("findAll returns all users when successful")
    @Sql(scripts = "/sql/init_one_profile.sql")
    void findAll_returnsAllUsers_WhenSuccessful() {
        List<Profile> profiles = repository.findAll();
        Assertions.assertThat(profiles)
                .isNotNull()
                .isNotEmpty();
    }


}

