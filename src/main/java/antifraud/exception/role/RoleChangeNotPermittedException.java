package antifraud.exception.role;

public class RoleChangeNotPermittedException extends RuntimeException {
    public RoleChangeNotPermittedException(String message) {
        super(message);
    }
}
