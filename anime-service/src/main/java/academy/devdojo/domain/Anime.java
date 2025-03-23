package academy.devdojo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Anime {
    private Long id;
    private String name;
    private static List<Anime> animeList = new ArrayList<>();

    static {
        Anime anime1 = Anime.builder().id(1L).name("Saiki Kusuo no Psi Nan").build();
        Anime anime2 = Anime.builder().id(2L).name("Dragon Ball Z").build();
        Anime anime3 = Anime.builder().id(3L).name("Sword Art Online").build();
        Anime anime4 = Anime.builder().id(4L).name("Shangri-la Frontiers").build();
        animeList.add(anime1);
        animeList.add(anime2);
        animeList.add(anime3);
        animeList.add(anime4);
    }

    public static List<Anime> getAnimes() {
        return animeList;
    }
}
