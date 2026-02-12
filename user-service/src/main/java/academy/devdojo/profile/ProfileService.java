package academy.devdojo.profile;

import academy.devdojo.domain.Profile;
import academy.devdojo.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    ProfileRepository repository;

    public Profile findByIdOrThrowNotFound(Long id){
        return repository.findById(id).orElseThrow(()->new NotFoundException("Profile not found"));
    }

    public void assertProfileExists(Profile profile){
        findByIdOrThrowNotFound(profile.getId());
    }

}
