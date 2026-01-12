package antifraud.exception.transaction;

public class TransactionFeedbackExistsException extends RuntimeException {
    public TransactionFeedbackExistsException(String message) {
        super(message);
    }
}
