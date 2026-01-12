package antifraud.exception.transaction;

public class TransactionFeedbackConflictException extends RuntimeException {
    public TransactionFeedbackConflictException(String message) {
        super(message);
    }
}
