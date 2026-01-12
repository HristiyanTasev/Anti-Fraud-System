package antifraud.exception.ip;

public class SuspiciousIpNotFoundException extends RuntimeException {
    public SuspiciousIpNotFoundException(String message) {
        super(message);
    }
}
