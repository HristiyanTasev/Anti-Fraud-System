package antifraud.validation.ip;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IpV4Validator implements ConstraintValidator<ValidIpV4, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // letting @NotBlank/@NotNull handle null if you want
        }
        String ip = value.trim();
        if (ip.isEmpty()) {
            return true;  // letting @NotBlank handle empty
        }

        String[] parts = ip.split("\\.", -1);
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            if (part.isEmpty()) return false;
            // no leading +/-, only digits
            for (int i = 0; i < part.length(); i++) {
                char c = part.charAt(i);
                if (c < '0' || c > '9') return false;
            }
            // avoid "01" etc. (strict IPv4)
            if (part.length() > 1 && part.charAt(0) == '0') {
                return false;
            }

            try {
                int n = Integer.parseInt(part);
                if (n < 0 || n > 255) {
                    return false;
                }
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return true;
    }
}