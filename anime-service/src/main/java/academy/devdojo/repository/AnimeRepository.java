package academy.devdojo.repository;

import academy.devdojo.domain.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    List<Anime> findByNameIgnoreCaseContaining(String name);
    Optional<Anime> findByNameIgnoreCase(String name);
    Optional<Anime> findByNameAndIdNot(String name, Long id);
}

