package academy.devdojo.profile;


import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.domain.Profile;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@WebMvcTest(ProfileController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan({"academy.devdojo.profile", "academy.devdojo.commons"})
class ProfileControllerTest {

    private final String URL = "/v1/profiles";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileUtils profileUtils;

    @Autowired
    private FileUtils fileUtils;

    @MockitoBean
    private ProfileRepository repository;

    private List<Profile> profileList;

    @BeforeEach
    void init() {
        profileList = profileUtils.newProfileList();
    }

    @Test
    @DisplayName("GET /v1/profiles returns a list with all profiles when argument is null")
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenArgumentIsNull() throws Exception {
        var response = fileUtils.readSourceFile("profiles/get-profiles-200.json");

        BDDMockito.when(repository.findAll()).thenReturn(profileList);

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("GET /v1/profiles?name=Admin returns a list with found Profile when name exists")
    @Order(2)
    void findAll_ReturnsListWithFoundProfile_WhenNameExists() throws Exception {
        var response = fileUtils.readSourceFile("profiles/get-profiles-name-admin-200.json");

        var name = "Admin";
        var expectedProfile = profileList.stream().filter(profile -> profile.getName().equals(name)).findFirst().orElse(null);

        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.singletonList(expectedProfile));

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET /v1/profiles?name=not-found returns empty list when name is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNameIsNotFound() throws Exception {
        var response = fileUtils.readSourceFile("profiles/get-profiles-name-not-found-200.json");

        var name = "not-found";

        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET /v1/profiles/paginated returns paginated list of Profiles")
    @Order(4)
    void findAllPaginated_ReturnAllPaginatedProfiles_WhenSuccessful() throws Exception {
        var response = fileUtils.readSourceFile("profiles/get-profiles-paginated-200.json");
        PageRequest pageRequest = PageRequest.of(0, 20);
        PageImpl<Profile> pageProfile = new PageImpl<>(profileList, pageRequest, 1);
        BDDMockito.when(repository.findAll(BDDMockito.any(Pageable.class))).thenReturn(pageProfile);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/paginated"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(5)
    @DisplayName("GET /v1/profiles/1 returns Profile when successful")
    void findById_ReturnsProfile_WhenSuccessful() throws Exception {
        var response = fileUtils.readSourceFile("profiles/get-profiles-by-id-1-200.json");
        var expectedId = 1L;
        Profile expectedProfile = profileList.stream().filter(profile -> profile.getId().equals(expectedId)).findFirst().orElse(null);

        BDDMockito.when(repository.findById(expectedId)).thenReturn(Optional.of(expectedProfile));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", expectedId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(6)
    @DisplayName("GET /v1/profiles/1 throws ResponseStatusException 404 when Profile is not found")
    void findById_ThrowsResponseStatusException_WhenProfileIsNotFound() throws Exception {
        var response = fileUtils.readSourceFile("profiles/get-profiles-by-id-99-404.json");
        var expectedId = 99L;

        BDDMockito.when(repository.findById(expectedId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", expectedId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @Order(7)
    @DisplayName("POST v1/profiles save a Profile when successful")
    void save_SaveAProfile_WhenSuccessful() throws Exception {
        var request = fileUtils.readSourceFile("profiles/post-request-profiles-201.json");
        var response = fileUtils.readSourceFile("profiles/post-response-profiles-201.json");

        var expectedProfileCreated = profileUtils.newProfileToSave().withId(99L);
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(expectedProfileCreated);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @ParameterizedTest
    @MethodSource("postProfileBadRequestSource")
    @Order(8)
    @DisplayName("POST v1/profiles returns bad request when fields are empty or invalid")
    void save_ReturnsBadRequest_WhenFieldsAreEmptyOrInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readSourceFile("profiles/%s".formatted(fileName));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();
        Assertions.assertThat(resolvedException).isNotNull();
        Assertions.assertThat(resolvedException.getMessage()).contains(errors);

    }

    private static Stream<Arguments> postProfileBadRequestSource() {
        var allRequiredErrors = allRequiredErrors();

        return Stream.of(
                Arguments.of("post-request-profile-empty-fields-400.json", allRequiredErrors),
                Arguments.of("post-request-profile-blank-fields-400.json", allRequiredErrors)
        );
    }

    private static List<String> allRequiredErrors() {
        var nameRequiredError = "The field 'name' is required";
        var descriptionRequiredError = "The field 'description' is required";
        return new ArrayList<>(List.of(nameRequiredError, descriptionRequiredError));
    }


}