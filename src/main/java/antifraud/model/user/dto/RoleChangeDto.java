package antifraud.model.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleChangeDto {
    @NotBlank
    private String username;
    @NotBlank
    private String role;
}
