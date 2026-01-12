package antifraud.exception;

import antifraud.exception.card.StolenCardExistsException;
import antifraud.exception.card.StolenCardNotFoundException;
import antifraud.exception.ip.SuspiciousIpExistsException;
import antifraud.exception.ip.SuspiciousIpNotFoundException;
import antifraud.exception.role.RoleAlreadyProvidedException;
import antifraud.exception.role.RoleChangeNotPermittedException;
import antifraud.exception.transaction.TransactionFeedbackConflictException;
import antifraud.exception.transaction.TransactionFeedbackExistsException;
import antifraud.exception.transaction.TransactionNotFoundException;
import antifraud.exception.user.UserExistsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalValidationExceptionHandler {

    // VALIDATION (DTO body: @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // VALIDATION (method params: @PathVariable / @RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Void> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // USER EXCEPTIONS
    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<Void> handleUserExistsException(UserExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    // ROLE EXCEPTIONS
    @ExceptionHandler(RoleChangeNotPermittedException.class)
    public ResponseEntity<Void> handleRoleChangeNotPermittedException(RoleChangeNotPermittedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(RoleAlreadyProvidedException.class)
    public ResponseEntity<Void> handleRoleAlreadyProvidedException(RoleAlreadyProvidedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    // SUSPICIOUS IP EXCEPTIONS
    @ExceptionHandler(SuspiciousIpExistsException.class)
    public ResponseEntity<Void> handleSuspiciousIpExistsException(SuspiciousIpExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(SuspiciousIpNotFoundException.class)
    public ResponseEntity<Void> handleSuspiciousIpNotFoundException(SuspiciousIpNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // STOLEN CARD EXCEPTIONS
    @ExceptionHandler(StolenCardExistsException.class)
    public ResponseEntity<Void> handleStolenCardExistsException(StolenCardExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(StolenCardNotFoundException.class)
    public ResponseEntity<Void> handleStolenCardNotFoundException(StolenCardNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // WRONG ENUM DTO EXCEPTION
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // TRANSACTION EXCEPTIONS
    @ExceptionHandler(TransactionFeedbackExistsException.class)
    public ResponseEntity<Void> handleTransactionFeedbackExistsException(TransactionFeedbackExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Void> handleTransactionNotFoundException(TransactionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(TransactionFeedbackConflictException.class)
    public ResponseEntity<Void> handleTransactionFeedbackConflictException(TransactionFeedbackConflictException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
}
