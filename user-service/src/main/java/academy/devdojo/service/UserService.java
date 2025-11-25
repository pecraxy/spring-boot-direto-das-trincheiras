package academy.devdojo.service;

import academy.devdojo.domain.User;
import academy.devdojo.exception.EmailAlreadyExistsException;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public List<User> findAll(@Nullable String firstName) {
        return firstName == null ? repository.findAll() : repository.findByFirstNameEqualsIgnoreCase(firstName);
    }

    public User findByIdOrElseThrowNotFoundException(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User save(User user) {
        assertEmailDoesNotExists(user.getEmail());
        return repository.save(user);
    }

    public void delete(Long id) {
        User userToDelete = this.findByIdOrElseThrowNotFoundException(id);
        repository.delete(userToDelete);
    }

    public void update(User userToUpdate) {
        assertUserExists(userToUpdate.getId());
        assertEmailDoesNotExists(userToUpdate.getEmail(), userToUpdate.getId());
        repository.save(userToUpdate);
    }

    public void assertUserExists(Long id) {
        this.findByIdOrElseThrowNotFoundException(id);
    }

    public void assertEmailDoesNotExists(String email) {
        repository.findByEmail(email)
                .ifPresent(this::throwEmailAlreadyExistsException);
    }

    public void assertEmailDoesNotExists(String email, Long id) {
        repository.findByEmailAndIdNot(email, id)
                .ifPresent(this::throwEmailAlreadyExistsException);
    }

    private void throwEmailAlreadyExistsException(User user) {
        throw new EmailAlreadyExistsException("Email %s already exists".formatted(user.getEmail()));
    }

}
