package antifraud.exception.ip;

public class SuspiciousIpExistsException extends RuntimeException {
    public SuspiciousIpExistsException(String message) {
        super(message);
    }
}
