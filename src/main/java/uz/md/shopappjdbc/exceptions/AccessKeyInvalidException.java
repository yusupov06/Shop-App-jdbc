package uz.md.shopappjdbc.exceptions;

public class AccessKeyInvalidException extends RuntimeException {
    public AccessKeyInvalidException(String message) {
        super(message);
    }
}
