package academy.devdojo.service;

import academy.devdojo.domain.User;
import academy.devdojo.repository.UserHardCodedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    public final UserHardCodedRepository repository;

    public List<User> findAll(@Nullable String firstName, @Nullable String lastName, @Nullable String email) {
        if (firstName != null && lastName != null) return repository.findByName(firstName, lastName);
        if (email != null) return repository.findByEmail(email);
        return repository.findAll();
    }

    public User findByIdOrElseThrowResponseStatusException(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User save(User user) {
        return repository.save(user);
    }

    public void delete(Long id) {
        User userToDelete = this.findByIdOrElseThrowResponseStatusException(id);
        repository.delete(userToDelete);
    }

    public void update(User userToUpdate) {
        repository.update(userToUpdate);
    }

}
