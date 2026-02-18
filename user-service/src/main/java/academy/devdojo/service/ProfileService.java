package academy.devdojo.service;

import academy.devdojo.domain.Profile;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.ProfileRepository;
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

    public List<Profile> findAll() {
        return repository.findAll();
    }

    public Profile save(Profile profile) {
        return repository.save(profile);
    }

}
