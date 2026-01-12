package antifraud.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDtoOut {
    private Long id;
    private String name;
    private String username;
    private String role;
}
