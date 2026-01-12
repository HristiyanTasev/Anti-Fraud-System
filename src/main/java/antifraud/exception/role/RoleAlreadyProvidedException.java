package antifraud.exception.role;

public class RoleAlreadyProvidedException extends RuntimeException {
    public RoleAlreadyProvidedException(String message) {
        super(message);
    }
}
