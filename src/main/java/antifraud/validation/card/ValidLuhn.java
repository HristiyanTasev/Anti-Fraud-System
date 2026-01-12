package antifraud.validation.card;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LuhnValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLuhn {
    String message() default "Invalid card number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}