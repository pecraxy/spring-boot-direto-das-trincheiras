package academy.devdojo.service;

import academy.devdojo.domain.Anime;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.exception.ObjectAlreadyExistsException;
import academy.devdojo.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository repository;

    public List<Anime> findAll(String name) {
        return name == null ? repository.findAll() : repository.findByNameEqualsIgnoreCase(name);
    }

    public Anime findByIdOrThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Anime not found"));
    }

    public Anime save(Anime anime) {
        assertAnimeNameDoesNotExists(anime.getName());
        return repository.save(anime);
    }

    public void delete(Long id) {
        Anime producerToDelete = this.findByIdOrThrowNotFound(id);
        repository.delete(producerToDelete);
    }

    public void update(Anime animeToUpdate) {
        assertAnimeExists(animeToUpdate.getId());
        assertAnimeNameDoesNotExists(animeToUpdate.getName(), animeToUpdate.getId());
        repository.save(animeToUpdate);
    }

    public void assertAnimeExists(Long id) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Anime not found"));
    }

    public void assertAnimeNameDoesNotExists(String name) {
        repository.findByNameIgnoreCase(name).ifPresent(this::throwAnimeAlreadyExists);
    }

    public void assertAnimeNameDoesNotExists(String name, Long id) {
        repository.findByNameAndIdNot(name, id).ifPresent(this::throwAnimeAlreadyExists);
    }

    private void throwAnimeAlreadyExists(Anime anime) {
        throw new ObjectAlreadyExistsException("Anime '%s' already exists".formatted(anime.getName()));
    }

}
