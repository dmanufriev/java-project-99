package hexlet.code.exception;

public class PasswordHashException extends RuntimeException {
    public PasswordHashException(String message) {
        super(message);
    }
}
