package antifraud.exception.card;

public class StolenCardNotFoundException extends RuntimeException {
    public StolenCardNotFoundException(String message) {
        super(message);
    }
}
