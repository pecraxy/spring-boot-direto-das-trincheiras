package academy.devdojo.controller;

import academy.devdojo.domain.Anime;
import academy.devdojo.repository.AnimeData;
import academy.devdojo.repository.AnimeHardCodedRepository;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebMvcTest(controllers = AnimeController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "academy.devdojo")
class AnimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private AnimeHardCodedRepository repository;

    @MockitoBean
    private AnimeData animeData;

    @Autowired
    private ResourceLoader resourceLoader;

    private List<Anime> animeList;

    @BeforeEach
    void init() {
        Anime anime1 = Anime.builder().id(1L).name("Naruto").build();
        Anime anime2 = Anime.builder().id(2L).name("Dragon Ball Z").build();
        Anime anime3 = Anime.builder().id(3L).name("Sword Art Online").build();
        Anime anime4 = Anime.builder().id(4L).name("Shangri-la Frontiers").build();

        animeList = new ArrayList<>((List.of(anime1, anime2, anime3, anime4)));
    }

    @Test
    @DisplayName("GET v1/animes returns a list with all animes when argument is null")
    @Order(1)
    void findAll_ReturnAllAnimes_WhenArgumentIsNull() throws Exception {
        BDDMockito.when(animeData.getAnimeList()).thenReturn(animeList);
        var response = readResourceFile("anime/get-animes-null-name-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/producers?name=Naruto returns a list with found object when name exists")
    @Order(2)
    void findAll_ReturnFoundAnime_WhenAnimeExists() throws Exception {
        BDDMockito.when(animeData.getAnimeList()).thenReturn(animeList);
        var response = readResourceFile("anime/get-animes-naruto-200.json");
        var name = "Naruto";
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes").param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/animes?name=x returns a empty list when name is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenAnimeIsNotFound() throws Exception {
        BDDMockito.when(animeData.getAnimeList()).thenReturn(animeList);
        var response = readResourceFile("anime/get-animes-x-200.json");
        var name = "x";
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes").param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/animes/1 returns an anime with given id when successful")
    @Order(4)
    void finById_ReturnsAnimeById_WhenSuccessful() throws Exception {
        BDDMockito.when(animeData.getAnimeList()).thenReturn(animeList);
        var id = animeList.getFirst().getId();
        var response = readResourceFile("anime/get-anime-by-id-1-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/animes/99 throws ResponseStatusException 404 when anime is not found")
    @Order(5)
    void findById_ThrowsResponseStatusException_WhenAnimeIsNotFound() throws Exception {
        BDDMockito.when(animeData.getAnimeList()).thenReturn(animeList);
        var id = 99L;
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Anime not found"));
    }

    @Test
    @DisplayName("POST v1/animes creates an anime")
    @Order(6)
    void save_CreatesAnAnime_WhenSuccessful() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Anime not found"));
    }

    private String readResourceFile(String fileName) throws IOException {
        var file = resourceLoader.getResource("classpath:%s".formatted(fileName)).getFile();
        return new String(Files.readAllBytes(file.toPath()));
    }

}