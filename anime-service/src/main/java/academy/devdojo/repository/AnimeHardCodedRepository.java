package academy.devdojo.repository;

import academy.devdojo.domain.Anime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class AnimeHardCodedRepository {

    private final AnimeData animeData;

    public List<Anime> findAll() {
        return animeData.getAnimeList();
    }

    public Optional<Anime> findById(Long id) {
        return animeData.getAnimeList().stream().filter(anime -> anime.getId().equals(id)).findFirst();
    }

    public List<Anime> findByName(String name) {
        return animeData.getAnimeList().stream().filter(anime -> anime.getName().equalsIgnoreCase(name)).toList();
    }

    public Anime save(Anime anime) {
        animeData.getAnimeList().add(anime);
        return anime;
    }

    public void delete(Anime anime) {
        animeData.getAnimeList().remove(anime);
    }

    public void update(Anime anime) {
        delete(anime);
        save(anime);
    }

}

