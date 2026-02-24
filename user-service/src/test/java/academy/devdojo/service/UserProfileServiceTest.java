package academy.devdojo.service;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.commons.UserProfileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.repository.UserProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService service;

    @InjectMocks
    private UserProfileUtils userProfileUtils;

    @Spy
    private ProfileUtils profileUtils;

    @Spy
    private UserUtils userUtils;

    @Mock
    private UserProfileRepository repository;

    private List<UserProfile> userProfileList;

    @BeforeEach
    void init() {
        userProfileList = userProfileUtils.newUserProfileList();
    }

    @Test
    @Order(1)
    @DisplayName("findAll returns a list with all user profiles")
    void findAll_ReturnAllUserProfiles_WhenSuccessful() {
        BDDMockito.when(repository.findAll()).thenReturn(userProfileList);
        var userProfiles = service.findAll();
        Assertions.assertThat(userProfiles)
                .isNotEmpty()
                .isNotNull()
                .hasSize(userProfileList.size())
                .hasSameElementsAs(userProfileList);
        userProfiles.forEach(userProfile -> Assertions.assertThat(userProfile).hasNoNullFieldsOrProperties());
    }

    @Test
    @Order(2)
    @DisplayName("findAllUsersByProfileId() returns a list of users for a given profile id")
    void findAllUsersByProfileId_ReturnsAllUsersForGivenProfileId_WhenSuccessful() {
        var profileId = 99L;
        List<User> usersByProfile = this.userProfileList.stream()
                .filter(userProfile -> userProfile.getProfile().getId().equals(profileId))
                .map(UserProfile::getUser)
                .toList();

        BDDMockito.when(repository.findAllUsersByProfileId(profileId)).thenReturn(usersByProfile);

        var users = service.findAllUsersByProfileId(profileId);

        Assertions.assertThat(users)
                .isNotEmpty()
                .doesNotContainNull()
                .hasSize(usersByProfile.size())
                .hasSameElementsAs(usersByProfile);
        users.forEach(user -> Assertions.assertThat(user).hasNoNullFieldsOrProperties());
    }


}