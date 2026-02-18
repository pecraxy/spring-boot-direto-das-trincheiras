package academy.devdojo.service;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.domain.Profile;
import academy.devdojo.repository.ProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileServiceTest {

    @InjectMocks
    private ProfileService service;

    @InjectMocks
    private ProfileUtils profileUtils;

    @Mock
    private ProfileRepository repository;

    private List<Profile> profileList;

    @BeforeEach
    void init() {
        profileList = profileUtils.newProfileList();
    }

    @Test
    @Order(1)
    @DisplayName("findAll returns a list with all profiles")
    void findAll_ReturnAllProfiles_WhenSuccessful() {
        BDDMockito.when(repository.findAll()).thenReturn(profileList);
        var producers = service.findAll();
        Assertions.assertThat(producers)
                .isNotEmpty()
                .isNotNull()
                .hasSize(profileList.size())
                .hasSameElementsAs(profileList);
    }

    @Test
    @Order(2)
    @DisplayName("save save a Profile when successful")
    void save_SaveAProfile_WhenSuccessful() {
        Profile newProfileToCreate = profileUtils.newProfileToSave();
        Profile profileSaved = profileUtils.newProfileSaved();

        BDDMockito.when(repository.save(BDDMockito.any())).thenReturn(profileSaved);

        Profile savedProfile = service.save(newProfileToCreate);

        Assertions.assertThat(savedProfile)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .isEqualTo(profileSaved);
    }


}