package academy.devdojo.repository;

import academy.devdojo.domain.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {
    List<Producer> findByNameEqualsIgnoreCase(String name);
    Optional<Producer> findByNameIgnoreCase(String name);
    Optional<Producer> findByNameAndIdNot(String name, Long id);
}

