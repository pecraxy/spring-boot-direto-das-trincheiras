package academy.devdojo.profile;

import academy.devdojo.domain.Profile;
import academy.devdojo.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository repository;

    public List<Profile> findAll(@Nullable String name){
        return name == null ? repository.findAll() : repository.findByName(name);
    }

    public Page<Profile> findAllPaginated(Pageable pageable){
        return repository.findAll(pageable);
    }

    public Profile save(Profile profile){
        return repository.save(profile);
    }

    public Profile findByIdOrThrowResponseStatusException(Long id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Profile not found"));
    }

}
