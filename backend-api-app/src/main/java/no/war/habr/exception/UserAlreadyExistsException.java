package no.war.habr.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super(String.format("User with username [%s] already exists", email));
    }
}
