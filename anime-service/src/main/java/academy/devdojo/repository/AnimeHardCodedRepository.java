package academy.devdojo.repository;

import academy.devdojo.domain.Anime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimeHardCodedRepository {

    private static final List<Anime> ANIMES = new ArrayList<>();

    static {
        Anime anime1 = Anime.builder().id(1L).name("Saiki Kusuo no Psi Nan").build();
        Anime anime2 = Anime.builder().id(2L).name("Dragon Ball Z").build();
        Anime anime3 = Anime.builder().id(3L).name("Sword Art Online").build();
        Anime anime4 = Anime.builder().id(4L).name("Shangri-la Frontiers").build();
        ANIMES.addAll(List.of(anime1, anime2, anime3, anime4));
    }

    public List<Anime> findAll(){
        return ANIMES;
    }

    public Optional<Anime> findById(Long id){
        return ANIMES.stream().filter(anime -> anime.getId().equals(id)).findFirst();
    }

    public List<Anime> findByName(String name){
        return ANIMES.stream().filter(anime -> anime.getName().contains(name)).toList();
    }

    public Anime save(Anime anime){
        ANIMES.add(anime);
        return anime;
    }

    public void delete(Anime anime){
        ANIMES.remove(anime);
    }

    public void update(Anime anime){
        delete(anime);
        save(anime);
    }

}

