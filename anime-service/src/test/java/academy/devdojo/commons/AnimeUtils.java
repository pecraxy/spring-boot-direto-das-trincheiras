package academy.devdojo.commons;

import academy.devdojo.domain.Anime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class AnimeUtils {
    public List<Anime> newAnimeList(){
        Anime anime1 = Anime.builder().id(1L).name("Naruto").build();
        Anime anime2 = Anime.builder().id(2L).name("Dragon Ball Z").build();
        Anime anime3 = Anime.builder().id(3L).name("Sword Art Online").build();
        Anime anime4 = Anime.builder().id(4L).name("Shangri-la Frontiers").build();
        return new ArrayList<>(List.of(anime1, anime2, anime3, anime4));
    }

    public Anime newAnimeToSave(){
        return Anime.builder().id(99L).name("Dungeon Ni Deaii").build();
    }
}
