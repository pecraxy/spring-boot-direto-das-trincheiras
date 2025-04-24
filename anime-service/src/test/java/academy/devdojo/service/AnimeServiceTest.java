package academy.devdojo.service;

import academy.devdojo.commons.AnimeUtils;
import academy.devdojo.domain.Anime;
import academy.devdojo.repository.AnimeHardCodedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeServiceTest {
    @InjectMocks
    private AnimeService service;

    @Mock
    private AnimeHardCodedRepository repository;

    @InjectMocks
    private AnimeUtils animeUtils;

    private List<Anime> animeList;

    @BeforeEach
    void init() {
        animeList = animeUtils.newAnimeList();
    }

    @Test
    @DisplayName("findAll returns a list with all animes when argument is null")
    @Order(1)
    void findAll_ReturnAllAnimes_WhenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(animeList);
        var animes = service.findAll(null);
        Assertions.assertThat(animes)
                .hasSize(animeList.size())
                .hasSameElementsAs(animeList);
    }

    @Test
    @DisplayName("findAll returns a list with found object when name exists")
    @Order(2)
    void findAll_ReturnFoundAnime_WhenNameExists() {
        var anime = animeList.getFirst();

        List<Anime> expectedAnimesFound = Collections.singletonList(anime);

        BDDMockito.when(repository.findByName(anime.getName()))
                .thenReturn(expectedAnimesFound);

        List<Anime> animesFound = service.findAll(anime.getName());

        Assertions.assertThat(animesFound)
                .hasSize(expectedAnimesFound.size())
                .hasSameElementsAs(expectedAnimesFound)
                .containsAll(expectedAnimesFound);
    }

    @Test
    @DisplayName("findAll returns empty list when name is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNameIsNotFound() {
        var name = "not-found";
        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.emptyList());
        List<Anime> foundAnimes = service.findAll(name);
        Assertions.assertThat(foundAnimes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns a anime with given id when successful")
    @Order(4)
    void findById_ReturnsAnimeById_WhenSuccessful() {
        var expectedAnime = animeList.getFirst();
        BDDMockito.when(repository.findById(expectedAnime.getId())).thenReturn(Optional.of(expectedAnime));
        Anime foundAnime = service.findByIdOrThrowNotFound(expectedAnime.getId());
        Assertions.assertThat(foundAnime)
                .isEqualTo(expectedAnime)
                .isNotNull();
    }

    @Test
    @DisplayName("findById throws ResponseStatusException when anime is not found")
    @Order(5)
    void findById_ThrowsResponseStatusException_WhenAnimeIsNotFound() {
        var expectedAnime = animeList.getFirst();
        BDDMockito.when(repository.findById(expectedAnime.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findByIdOrThrowNotFound(expectedAnime.getId()))
                .isInstanceOf(ResponseStatusException.class);

    }

    @Order(6)
    @Test
    @DisplayName("save creates a anime")
    void save_CreatesAAnime_WhenSuccesful() {
        var animeToSave = animeUtils.newAnimeToSave();

        BDDMockito.when(repository.save(animeToSave)).thenReturn(animeToSave);

        Anime savedAnime = service.save(animeToSave);

        Assertions.assertThat(savedAnime).isEqualTo(animeToSave).hasNoNullFieldsOrProperties();
    }

    @Order(7)
    @Test
    @DisplayName("delete deletes a anime")
    void delete_DeletesAAnime_WhenSuccesful() {
        var expectedAnimeToDelete = animeList.getFirst();

        BDDMockito.when(repository.findById(expectedAnimeToDelete.getId())).thenReturn(Optional.of(expectedAnimeToDelete));

        BDDMockito.doNothing().when(repository).delete(expectedAnimeToDelete);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.delete(expectedAnimeToDelete.getId()));
    }

    @Order(8)
    @Test
    @DisplayName("delete throws ResponseStatusException when anime is not found")
    void delete_ThrowsResponseStatusException_WhenAnimeNotFound() {
        var expectedAnimeToDelete = animeList.getFirst();

        BDDMockito.when(repository.findById(expectedAnimeToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(expectedAnimeToDelete.getId()))
                .isInstanceOf(ResponseStatusException.class);


    }

    @Order(9)
    @Test
    @DisplayName("update update an anime")
    void update_UpdateAnime_WhenSuccesfull() {
        var animeToUpdate = animeList.getFirst();

        animeToUpdate.setName("Berserk 2");

        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(animeToUpdate));

        BDDMockito.doNothing().when(repository).update(animeToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(animeToUpdate));
    }

    @Order(10)
    @Test
    @DisplayName("update throws ResponseStatusException when anime is not found")
    void update_ThrowsResponseStatusException_WhenAnimeNotFound() {
        var animeToUpdate = animeList.getFirst();

        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(animeToUpdate))
                .isInstanceOf(ResponseStatusException.class);
    }
}