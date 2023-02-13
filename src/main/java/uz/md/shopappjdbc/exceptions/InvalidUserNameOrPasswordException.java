package uz.md.shopappjdbc.exceptions;

public class InvalidUserNameOrPasswordException extends RuntimeException {
    public InvalidUserNameOrPasswordException(String message) {
        super(message);
    }
}
