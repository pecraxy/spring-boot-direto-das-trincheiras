package academy.devdojo.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Anime {
    private Long id;
    private String name;

    public static List<Anime> getAnimes(){
        Anime anime1 = Anime.builder().id(1L).name("Saiki Kusuo no Psi Nan").build();
        Anime anime2 = Anime.builder().id(2L).name("Dragon Ball Z").build();
        Anime anime3 = Anime.builder().id(3L).name("Sword Art Online").build();
        Anime anime4 = Anime.builder().id(4L).name("Shangri-la Frontiers").build();
        return List.of(anime1, anime2, anime3, anime4);
    }
}
