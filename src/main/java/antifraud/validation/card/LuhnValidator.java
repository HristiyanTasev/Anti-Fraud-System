package antifraud.validation.card;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LuhnValidator implements ConstraintValidator<ValidLuhn, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // letting @NotBlank/@NotNull handle null if you want
        }
        String s = value.trim();
        if (s.isEmpty()) {
            return true;   // letting @NotBlank handle empty
        }
        if (s.length() != 16) {
            return false;
        }

        // digits only
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }

        // Luhn checksum
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = s.length() - 1; i >= 0; i--) {
            int d = s.charAt(i) - '0';
            if (doubleDigit) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }
}