package antifraud.model.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDtoIn {
    @NotBlank
    private String name;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
