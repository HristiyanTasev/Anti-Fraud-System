package antifraud.validation.ip;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IpV4Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIpV4 {
    String message() default "Invalid IP address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}