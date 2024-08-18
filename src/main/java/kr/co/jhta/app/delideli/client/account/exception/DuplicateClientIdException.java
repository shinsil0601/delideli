package kr.co.jhta.app.delideli.client.account.exception;

public class DuplicateClientIdException extends RuntimeException {
    public DuplicateClientIdException(String message) {
        super(message);
    }
}
