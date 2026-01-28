package academy.devdojo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ObjectAlreadyExistsException extends ResponseStatusException {
    public ObjectAlreadyExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
