package antifraud.exception.card;

public class StolenCardExistsException extends RuntimeException {
    public StolenCardExistsException(String message) {
        super(message);
    }
}
