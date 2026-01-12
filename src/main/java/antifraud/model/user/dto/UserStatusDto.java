package antifraud.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatusDto {
    private String username;
    private String status;
}
