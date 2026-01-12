package antifraud.model.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessChangeDto {
    @NotBlank
    private String username;
    private AccessOperation operation;
}
