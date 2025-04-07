package academy.devdojo.repository;

import academy.devdojo.domain.Anime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeHardCodedRepositoryTest {
    @InjectMocks
    private AnimeHardCodedRepository repository;
    @Mock
    private AnimeData animeData;

    private List<Anime> animeList;

    @BeforeEach
    void init() {
        Anime anime1 = Anime.builder().id(1L).name("Saiki Kusuo no Psi Nan").build();
        Anime anime2 = Anime.builder().id(2L).name("Dragon Ball Z").build();
        Anime anime3 = Anime.builder().id(3L).name("Sword Art Online").build();
        Anime anime4 = Anime.builder().id(4L).name("Shangri-la Frontiers").build();

        animeList = new ArrayList<>((List.of(anime1, anime2, anime3, anime4)));

        BDDMockito.when(animeData.getAnimeList()).thenReturn(animeList);
    }

    @Test
    @DisplayName("findAll returns a list with all Animes")
    @Order(1)
    void findAll_ReturnAllAnimes_WhenSuccesful() {
        var animes = repository.findAll();

        Assertions.assertThat(animes)
                .hasSize(animeList.size())
                .hasSameElementsAs(animeList);
    }

    @Test
    @DisplayName("findById returns an Anime with given id")
    @Order(2)
    void findById_ReturnAnime_WhenSucessful_WithGivenId() {
        var expectedProducer = animeList.getFirst();

        var animeOptional = repository.findById(expectedProducer.getId());

        Assertions.assertThat(animeOptional)
                .isPresent()
                .contains(expectedProducer);
    }

    @Test
    @DisplayName("findByName returns a empty list when name is null")
    @Order(3)
    void findByName_ReturnEmptyList_WhenNameIsNull() {
        var animes = repository.findByName(null);

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findByName returns a list with found object when object name exists")
    @Order(4)
    void findByName_ReturnListWithFoundObject_WhenObjectNameExists() {
        var expectedAnime = animeList.getFirst();

        var foundAnimeList = repository.findByName(expectedAnime.getName());

        Assertions.assertThat(foundAnimeList)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedAnime);
    }

    @Test
    @DisplayName("save creates an anime")
    @Order(5)
    void save_CreatesAnime_WhenSuccesful() {
        Anime animeToSave = Anime.builder().id(5L).name("Berserk").build();

        Anime savedAnime = repository.save(animeToSave);

        Assertions.assertThat(savedAnime)
                .isEqualTo(animeToSave)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("delete removes an anime from list when succesful")
    @Order(6)
    void delete_RemoveAnime_WhenSuccesful() {
        var animeToDelete = repository.findAll().getFirst();

        repository.delete(animeToDelete);

        Assertions.assertThat(this.animeList)
                .doesNotContain(animeToDelete);

        List<Anime> animes = repository.findAll();

        Assertions.assertThat(animes)
                .isNotEmpty()
                .doesNotContain(animeToDelete);
    }

    @Test
    @DisplayName("update updates an anime")
    @Order(6)
    void update() {
        var animeToUpdate = this.animeList.getFirst();

        animeToUpdate.setName("Saiki Kusuo");

        repository.update(animeToUpdate);

        Assertions.assertThat(this.animeList)
                .contains(animeToUpdate);

        var animeUpdateOptional = repository.findById(animeToUpdate.getId());

        Assertions.assertThat(animeUpdateOptional)
                .isPresent();

        Assertions.assertThat(animeUpdateOptional.get().getName().equals(animeToUpdate.getName()));
    }

}