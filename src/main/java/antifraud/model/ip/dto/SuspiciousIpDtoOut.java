package antifraud.model.ip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuspiciousIpDtoOut {
    private Long id;
    private String ip;
}
