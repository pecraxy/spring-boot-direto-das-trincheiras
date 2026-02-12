package academy.devdojo.profile;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.domain.Profile;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    @DisplayName("findAll returns a list with all producers when arguments are null")
    void findAll_ReturnAllProfiles_WhenArgumentsAreNull(){
        BDDMockito.when(repository.findAll()).thenReturn(profileList);
        var producers = service.findAll(null);
        Assertions.assertThat(producers)
                .isNotEmpty()
                .isNotNull()
                .hasSize(profileList.size())
                .hasSameElementsAs(profileList);
    }

    @Test
    @Order(2)
    @DisplayName("findAll returns a list with found Profile when name exists")
    void findAll_returnsFoundProfile_WhenNameExists(){
        var expectedProfile = profileList.getFirst();
        var name = expectedProfile.getName();

        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.singletonList(expectedProfile));

        var foundProducer = service.findAll(name);

        Assertions.assertThat(foundProducer)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1)
                .contains(expectedProfile);
    }

    @Test
    @Order(3)
    @DisplayName("findAll returns empty list when name is not found")
    void findAll_returnsEmptyList_WhenNameIsNotFound(){
        var name = "not-found";

        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.emptyList());

        var foundProducer = service.findAll(name);

        Assertions.assertThat(foundProducer)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("findAllPaginated returns all paginated profiles when successful")
    void findAllPaginated_ReturnAllPaginatedProfiles_WhenSuccessful(){
        PageRequest pageRequest = PageRequest.of(0, profileList.size());
        PageImpl<Profile> pageProfile = new PageImpl<>(profileList, pageRequest, 1);

        BDDMockito.when(repository.findAll(BDDMockito.any(Pageable.class))).thenReturn(pageProfile);

        var foundProfiles = service.findAllPaginated(pageRequest);

        Assertions.assertThat(foundProfiles)
                .isNotNull()
                .isNotEmpty()
                .hasSameElementsAs(profileList);
    }

    @Test
    @Order(5)
    @DisplayName("findById returns Profile when successful")
    void findById_ReturnsProfile_WhenSuccessful(){
        var id = 1L;
        Profile expectedProfile = profileList.getFirst();

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(expectedProfile));
        Profile foundProfile = service.findByIdOrThrowResponseStatusException(id);

        Assertions.assertThat(foundProfile)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .isEqualTo(expectedProfile);
    }

    @Test
    @Order(6)
    @DisplayName("findById throws ResponseStatusException when Profile is not found")
    void findById_ThrowsResponseStatusException_WhenProfileIsNotFound(){
        var id = 99L;
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThatException()
                .isThrownBy(() -> service.findByIdOrThrowResponseStatusException(id))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @Order(6)
    @DisplayName("save save a Profile when successful")
    void save_SaveAProfile_WhenSuccessful(){
        Profile newProfileToCreate = profileUtils.newProfileToCreate().withId(99L);
        BDDMockito.when(repository.save(BDDMockito.any())).thenReturn(newProfileToCreate);
        Profile savedProfile = service.save(newProfileToCreate);

        Assertions.assertThat(savedProfile)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .isEqualTo(newProfileToCreate);
    }


}