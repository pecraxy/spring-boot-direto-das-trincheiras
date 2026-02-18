package academy.devdojo.controller;


import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.domain.Profile;
import academy.devdojo.repository.ProfileRepository;
import academy.devdojo.repository.UserProfileRepository;
import academy.devdojo.repository.UserRepository;
import academy.devdojo.service.UserProfileService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(ProfileController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan({"academy.devdojo"})
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

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserProfileRepository userProfileRepository;

    private List<Profile> profileList;

    @BeforeEach
    void init() {
        profileList = profileUtils.newProfileList();
    }

    @Test
    @DisplayName("GET /v1/profiles returns a list with all profiles")
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenSuccessful() throws Exception {
        var response = fileUtils.readSourceFile("profiles/get-profiles-200.json");

        BDDMockito.when(repository.findAll()).thenReturn(profileList);

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @Order(7)
    @DisplayName("POST v1/profiles save a Profile when successful")
    void save_SaveAProfile_WhenSuccessful() throws Exception {
        var request = fileUtils.readSourceFile("profiles/post-request-profiles-201.json");
        var response = fileUtils.readSourceFile("profiles/post-response-profiles-201.json");

        var expectedProfileSaved = profileUtils.newProfileSaved();
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(expectedProfileSaved);

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