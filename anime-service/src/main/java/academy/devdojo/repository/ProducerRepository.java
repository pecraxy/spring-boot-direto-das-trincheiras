package academy.devdojo.repository;

import academy.devdojo.domain.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {
    List<Producer> findByNameIgnoreCaseContaining(String name);
    Optional<Producer> findByName(String name);
    Optional<Producer> findByNameAndIdNot(String name, Long id);
}

